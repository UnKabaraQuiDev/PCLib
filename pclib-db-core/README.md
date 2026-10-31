# PCLib DB Core

`pclib-db-core` provides database mapping, SQL queries, schema scanning, migrations, and transactions. It is independent of Spring and requires Java 8.

It does not include a JDBC driver or a DBMS implementation. Add the provider you use, or use [`pclib-db-all`](../pclib-db-all/) for all three. For dependency injection, automatic table creation, and automatic `@Query` proxies, see [`pclib-db-spring`](../pclib-db-spring/).

## Dependencies and database providers

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-db-core</artifactId>
</dependency>
```

Versions can be managed by the [PCLib parent BOM](../#use-the-parent-pom--dependency-management).

| Module | Protocol | Connector | Configuration |
| --- | --- | --- | --- |
| [`pclib-db-mysql`](../pclib-db-mysql/) | `mysql` | `MySQLDatabaseConnector` | `username`, `password`, `host` (default `localhost`), `port` (default `3306`) |
| [`pclib-db-sqlite`](../pclib-db-sqlite/) | `sqlite` | `SQLiteDatabaseConnector` | `dirPath` (default `.`); database name becomes the file name |
| [`pclib-db-postgres`](../pclib-db-postgres/) | `postgresql` | `PostgreSQLDatabaseConnector` | `username`, `password`, `host` (default `localhost`), `port` (default `5432`), `maintenanceDatabase` (default `postgres`) |
| [`pclib-db-all`](../pclib-db-all/) | All of the above | \* | \* |

The provider supplies the SQL dialect, function resolver, column types, and JDBC connector. Providers are discovered through Java's `ServiceLoader`. You can register your own with `DbmsProviders.registerProvider(provider)`.

### Connect to a database

_Example for SQLite, the other modules work the same_

```java
SQLiteDatabaseConnector connector = new SQLiteDatabaseConnector("./data");
Database database = new Database(connector, "people.sqlite");
database.create();

// The table class is defined below.
PersonTable people = new PersonTable(database);
people.create();
```

## Define an entry and table

Fields marked with `@Column` become SQL columns. A column is non-null by default; use `@Nullable` to allow `NULL`-values.

```java
public class PersonData implements DatabaseEntry {

  @Column
  @PrimaryKey
  @AutoIncrement
  public long id;

  @Column
  @Unique
  public @MaxLength(80) String name;

  @Column
  @Nullable
  public @MaxLength(255) String note;

  public PersonData() {}

  public PersonData(String name) {
    this.name = name;
  }

}

public class PersonTable extends DatabaseTable<PersonData> {
  public PersonTable(Database database) {
    super(database);
  }
}
```

Then insert, load, or delete entries through the table:

```java
PersonData person = new PersonData("Alice");
people.insertAndReload(person); // Refresh generated and computed values
PersonData samePerson = people.load(person);
people.delete(samePerson);
```

`DatabaseTable` also provides methods such as `loadUnique`, `loadUniqueIfExists`, `loadByUnique`, `exists`, `update`, `updateAndReload`, `deleteUnique`, and `count`. Use `DatabaseView` and view annotations for read-only SQL views.

### Construct entries from query results

PCLib maps result rows to `DatabaseEntry` instances through `DefaultEntryInstanceProvider`. By default, it creates an entry with a no-argument constructor and fills its `@Column` fields. It can also use a public constructor or a static method annotated with `@Factory` when that method's parameters match the returned column names.

```java
public class AccountData implements DatabaseEntry {
    @Column public long id;
    @Column public String label;

    // Used when no parameter-based constructor or factory matches.
    public AccountData() {}

    // Matches a result containing both id and label.
    public AccountData(@Column(name = "id") long id,
                       @Column(name = "label") String label) {
        this.id = id;
        this.label = label;
    }

    // Matches a projection containing only label.
    @Factory
    public static AccountData fromLabel(@Column(name = "label") String value) {
        AccountData account = new AccountData();
        account.label = value;
        return account;
    }
}
```

The provider compares **sets of result column labels** with parameter names, so order does not matter. It derives names from Java parameters when compiled with `-parameters`, or from `@Column(name = "...")` on each parameter. Factory methods must be `static` and return the exact entry class; they may be non-public. By default, duplicate constructor/factory parameter sets are rejected. If you set `DatabaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY` to `false` through `utils.setOption(...)` before scanning, the constructor takes priority over a matching factory.

When it uses a matching constructor or factory, PCLib passes the decoded column values to that method. It does not then fill other fields from the result row. Use a no-argument constructor for ordinary field-based loading, and use factories for projections or entry classes that need custom construction.

### Entry lifecycle methods

PCLib also recognizes methods on an entry class annotated with `@Load`, `@Insert`, and `@Update`. These annotations are different from `SQLQueryableRule` hooks: they mark methods on the **entry**, not on the table.

```java
public class PersonData implements DatabaseEntry {
    @Column @PrimaryKey @AutoIncrement public long id;
    @Column public String name;

    public PersonData() {}

    @Load
    private void afterLoad() {
        // Runs after a row has been decoded into this instance.
        name = name == null ? null : name.trim();
    }

    @Insert
    private void afterInsert() {
        // Runs when PCLib fills the generated values after an insert.
    }
}
```

These callbacks take no arguments. PCLib can invoke non-public methods through reflection. The current implementation calls `@Load` after decoding a row, including rows constructed through a `@Factory` method, and calls `@Insert` while applying generated insert values. It can discover `@Update` methods, but the current update path **does not invoke them**. Use a [table hook rule](#sql-queryable-hooks-and-rules) if you need reliable update callbacks.

## Build tables at runtime with `DatabaseBuilder`

`DatabaseBuilder` defines tables and columns in Java code. Use it when the table structure is not represented only by `@Column` fields, such as a table assembled from configuration.

Each column needs a `ColumnType` for SQL storage and a `StorageBinding` for reading and writing the value in a `DatabaseEntry`. For a normal Java field, `FieldStorageBinding` handles the second part.

```java
// This example uses the PersonData class shown above.
DatabaseEntryUtils utils = database.getDatabaseEntryUtils();
DatabaseBuilder builder = new DatabaseBuilder(database);

builder.newTable()
    .name("builtPeople") // becomes `built_people` at runtime
    .entryClass(PersonData.class)
    .newColumn()
        .name("id")
        .primaryKey(true)
        .autoIncrement(true)
        .type(utils.getColumnTypeProvider().getTypeFor(long.class))
        .storagebinding(new FieldStorageBinding(PersonData.class.getDeclaredField("id")))
        .build()
    .newColumn()
        .name("name")
        .type(utils.getColumnTypeProvider().getTypeFor(String.class))
        .storagebinding(new FieldStorageBinding(PersonData.class.getDeclaredField("name")))
        .build()
    .build(); // Finish the table plan.

Database built = builder.build(); // Register the planned tables into the database's structure
built.scanFromBeans().createBeans((object, created) -> {});
```

The `newColumn().build()` call returns to the table plan; `newTable().build()` returns to the database builder. The final `builder.build()` returns the `Database`. Call `scanFromBeans()` before creating its tables.

Other table options include `explicitName`, `schema`, `tableId`, `definedName`, `targetClass`, `beanProvider`, and `put(key, value)` for custom hints. Column options include `nullable`, `uniqueGroupId`, `foreignKeyTable`, `check`, and `put`.

For entries backed by a `Map` or another storage model, implement `StorageBinding` instead of using `FieldStorageBinding`. The [`DBBuilderTest`](../pclib-db-all/src/test/java/test/DBBuilderTest.java) contains a map-backed example and shows how to attach query functions to a builder plan.

## What hints are

A **hint** is a named setting that PCLib uses while it scans a database model, chooses a column type, or builds SQL. Each hint has a key and a value. Annotations are the usual way to supply them; some hints can also be placed in a `Map<String, Object>` and passed to a `Database`, `DatabaseTable` or `DatabaseView` (customHints field, should be set before scanning).

### Hint scopes and hierarchy

Hints are organized by the part of the database they describe:

| Scope | Hint annotation | Common annotations or keys | Used for |
| --- | --- | --- | --- |
| Database | Programmatic `customHints` | `DefaultDatabaseHints.NAME_OVERRIDE`, `CHARACTER_SET`, `COLLATION`, `ENGINE`, `MIGRATION_NAME` | Database-level settings |
| Table or view (`SQLQueryable`) | `@QueryableHint` | `@NameOverride`, `@DefinedName`, `@Schema`, `@CharacterSet`, `@Collation`, `@Engine`, `@TableId` | Table/view naming and SQL generation |
| Column | `@ColumnHint` | `@PrimaryKey`, `@AutoIncrement`, `@Nullable`, `@Unique`, `@Index`, `@ForeignKey`, `@DefaultValue`, `@Check`, `@Generated`, `@Version` | Constraints and column behavior |
| Column type | `@TypeHint` | `@MaxLength`, `@FixedLength`, `@DecimalParam`, `@Unsigned`, `@TypeOverride` | Choosing the SQL and encoding types |
| Query method and parameters | `@QueryHint` | `@Query`, `@Param`, `@Limit`, `@Offset`, `@All`, `@Any`, `@NotNull`, `@OrIsNull` | Query generation and result handling |

This is a **scope hierarchy**. A database owns tables and views; a table owns columns; a column has a type. Hints are merged in this order, with later values overriding earlier ones: scanned entry < scanned queryable < database custom < entry custom < queryable custom. A `DatabaseEntry` can therefore behave differently depending on its table.

```java
@Column                 // The field belongs to the table.
@DefaultValue("'new'")  // Column hint: SQL default expression.
private @MaxLength(40) String status; // Type hint: selects a bounded string type -> VARCHAR(40)
```

PCLib collects hint annotations and their meta-annotations. A DBMS-specific value takes priority over an unqualified value. `dbms` accepts glob patterns such as `mysql`, `postgres*`, and `*sql`. Prefer constants like `MySQLDbmsProvider.DBMS_QUALIFIER_NAME`, `PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME`, and `SQLiteDbmsProvider.DBMS_QUALIFIER_NAME` for exact matches. A duplicate non-repeatable hint at the same matching level is an error. Repeatable and grouped hints allow a column to belong to several indexes or unique constraints.

### Example: one entry, two tables, different defaults

Several tables can reuse the same entry class while applying different hints. In this example, the `country` column reads `DEFAULT_COUNTRY` from the hints available to its table:

```java
public class PersonData implements DatabaseEntry {
    @Column @PrimaryKey @AutoIncrement
    public long id;

    @Column
    public @MaxLength(80) String name;

    @Column
    @DefaultValue("'{E:DEFAULT_COUNTRY}'")
    public @FixedLength(2) String country;

    public PersonData() {}
}

public class FrenchPersonTable extends DatabaseTable<PersonData> {
    public static final String DEFAULT_COUNTRY = "FR";

    public FrenchPersonTable(Database database) {
        super(database, "french_people");
        getCustomHints().put("DEFAULT_COUNTRY", DEFAULT_COUNTRY);
    }
}

// can also be set through an annotation
@QueryableHint(type = "DEFAULT_COUNTRY", value = "GB")
public class EnglishPersonTable extends DatabaseTable<PersonData> {

    public EnglishPersonTable(Database database) {
        super(database, "english_people");
    }
}

// no DEFAULT_COUNTRY is set here so it'll fall back to the database's hints, then the JVM properties, then the system env
public class ChinesePersonTable extends DatabaseTable<PersonData> {

    public ChinesePersonTable(Database database) {
        super(database, "chinese_people");
    }
}
```

Register and scan all three tables with the same database:

```java
database.getCustomHints().put("DEFAULT_COUNTRY", "ZZ"); // in this example
database.register(new FrenchPersonTable(database), new EnglishPersonTable(database), new ChinesePersonTable(database));
database.scanFromBeans();
database.createBeans((object, created) -> {});
```

The class constant on `FrenchPersonTable` becomes a hint when the constructor calls `getCustomHints().put(...)`. `EnglishPersonTable` supplies the same hint through `@QueryableHint`. `ChinesePersonTable` has no table-level value, so it uses the database's `ZZ` hint. Their SQL defaults resolve to `'FR'`, `'GB'`, and `'ZZ'`.

During scanning, PCLib merges the hints for each table. If no hint, Java system property, or environment variable provides `DEFAULT_COUNTRY`, `{E:DEFAULT_COUNTRY}` throws a `PropertyNotFoundException`. Use `{E:DEFAULT_COUNTRY:XX}` if you want `XX` as a fallback. Keep the quotes around the placeholder in `@DefaultValue` because the resulting country is an SQL string literal. A suffix outside the braces, as in `{E:DEFAULT_COUNTRY}:?`, is literal text, **not** fallback syntax.

### Type hints

Use type hints **on the Java type**, including query parameter types when needed:

```java
@Column public @MaxLength(128) String email; // -> VARCHAR(128)
@Column public @FixedLength(2) String countryCode; // -> CHAR(2)
@Column public @DecimalParam(precision = 12, scale = 2) BigDecimal amount; // -> DECIMAL(12, 2)
@Column public @Unsigned(dbms = "mysql") long count; // -> UNSIGNED BIGINTEGER

// Equivalent low-level form for the first hint:
@Column public @TypeHint(type = DefaultTypeHints.MAX_LENGTH, value = "128") String label;
```

`DefaultTypeHints` contains the standard keys: `MAX_LENGTH`, `FIXED_LENGTH`, `PRECISION`, `SCALE`, `TYPE_OVERRIDE`, `UNSIGNED`, and encoding hints such as `JSON`, `JSONB`, `UUID`, `DATE`, `TIME`, `TIMESTAMP`, `TIMEZONE`, and `INTERVAL`. Provider support varies; a hint does not guarantee that every DBMS implements the same SQL type.

The registries score possible `ColumnType` mappings. A matching length or precision hint usually selects a more specific mapping than the fallback. For instance, a plain `String` uses the provider's text mapping, while `@MaxLength(80) String` selects a length-limited string mapping.

### Column hints

| Annotation | Effect |
| --- | --- |
| `@PrimaryKey` | Includes the field in the primary key |
| `@AutoIncrement` | Generates an incrementing primary-key value |
| `@Nullable` | Allows `NULL` (columns are non-null by default) |
| `@Unique(group)` | Adds a unique constraint; fields with the same group form a composite unique key |
| `@Index(group)` | Adds an index; fields with the same group form a composite index |
| `@ForeignKey(table = ..., column = ...)` | Defines a reference, with optional group, update/delete actions, and deferral mode |
| `@DefaultValue` / `@RandomUuid` | Sets an SQL default expression; `@RandomUuid` provides DBMS-specific expressions |
| `@Check` | Adds a check constraint to a field or entry class |
| `@Generated` | Marks a computed column as `VIRTUAL` or `STORED`, using its `@DefaultValue` expression |
| `@OnUpdate` | Defines SQL update behavior _(eg. `@OnUpdate("(CURRENT_TIMESTAMP)")`)_ |
| `@UpdateExpression` | Overrides the value when updating _(eg. `{FIELD} + 1` will result in an SQL `UPDATE ... SET {FIELD} = {FIELD} + 1`)_ |
| `@Version` / `@TimeVersion` | Defines a version column for update tracking _(needs the `VersionRule` to be registered)_ |

Example of a composite unique constraint:

```java
@Column @Unique(1) public @MaxLength(32) String tenant;
@Column @Unique(1) public @MaxLength(160) String email;
```

The same group ID connects both columns. `@Unique(2)` on other fields creates another constraint. `@DefaultValue`, `@Check`, and the generic hint annotations can be used more than once for DBMS-specific expressions.

### Table/view hints

Use convenience annotations for common settings, or use `@QueryableHint` when there is no dedicated annotation:

```java
@NameOverride("customers") // name override can also be passed in the super-constructor
@Schema(value = "public", dbms = "postgresql")
@CharacterSet(value = "utf8mb4", dbms = "mysql")
@TableId("customers") // used for tracking tables when renaming during migration, defaults to the table name
public class CustomerTable extends DatabaseTable<CustomerData> {
  public CustomerTable(Database database) { super(database); }
}
```

`DefaultQueryableHints` contains keys for names, schemas, table IDs, and views. 

### Query hints

`@Query` describes an SQL query, while `@Param` maps method parameters to columns and comparators. Spring implements abstract query methods automatically. Without Spring, you can use the same annotations through a `QueryFunctionProvider` (see the next section):

```java
@Query("SELECT * FROM {NAME} WHERE {M:email} = ?")
public abstract Optional<CustomerData> byEmail(String email);

@Query
public abstract List<CustomerData> search(
    @Param(value = "name", comparator = "LIKE", ignoreNull = true) String name,
    @Limit int limit);
```

#### Query parameters and collections

`@Query` can generate a `SELECT` statement from its parameters, or you can provide SQL with `@Query("...")`. `@Param` maps a Java argument to a column. You can supply the column name when it differs from the Java parameter name.

The following method declarations fit in an abstract Spring repository. In plain `pclib-db-core`, use concrete methods backed by `ParamFunction`, as shown [below](#use-query-at-runtime-without-spring).

```java
// One column condition: WHERE name = ?
@Query
public abstract Optional<PersonData> byName(@Param("name") String name);

// Multiple conditions are combined with AND.
@Query
public abstract List<PersonData> search(
    @Param(value = "name", comparator = "LIKE", ignoreNull = true) String pattern,
    @Param(value = "id", comparator = ">=") long minId,
    @Limit int limit,
    @Offset int offset);

// Only returns rows matching one of the supplied names.
@Query
public abstract List<PersonData> byAnyName(@Param("name") @Any List<String> names);

// Arrays work as well. For collections, @Any is the default.
@Query
public abstract List<PersonData> byAnyNameArray(@Param("name") String[] names);

// Match the primary keys of the supplied PersonData entries.
@Query
public abstract List<PersonData> byAnyPerson(@Param @Any List<PersonData> people);

// Requires every value to match (usually only useful for special conditions).
@Query
public abstract List<PersonData> byAllNames(@Param("name") @All List<String> names);
```

| Parameter annotation | Behavior |
| --- | --- |
| `@Param("name")` | Uses the `name` column, even if the Java parameter is called something else. With no explicit name, PCLib reads the parameter name (compile with `-parameters`). |
| `@Param(comparator = "LIKE")` | Uses `LIKE` instead of `=`. Other supported comparators include `<`, `<=`, `>`, `>=`, `<>`, `IN`, and `IS DISTINCT FROM`. |
| `@Param(ignoreNull = true)` | Skips a collection condition when its value is `null` or empty; scalar null handling uses a generated null-aware condition. |
| `@Any` | Matches **at least one** item in a `Collection` or array. PCLib uses `IN (...)` when the provider supports it, otherwise OR-connected comparisons. Collections use this mode by default. |
| `@All` | Matches **every** item. PCLib joins the generated comparisons with `AND`. It cannot be combined with `@Any`. |
| `@Limit` / `@Offset` | Bind pagination arguments to generated `LIMIT` and `OFFSET` clauses; do not combine them with `@Param`. |

For example, `byAnyName(List.of("Alice", "Bob"))` generates a condition like `name IN (?, ?)`, with both names bound by JDBC. `byAllNames` instead generates `name = ? AND name = ?`. With two different names, no single row satisfies that condition. Collections of `DatabaseEntry` values use their primary-key or related foreign-key fields, rather than serializing entire objects into one SQL parameter.

PCLib builds the collection's placeholders from its size when the query runs, so a two-item list and a five-item list can produce different SQL. Use `@Param(ignoreNull = true)` when an optional collection should not add a filter if empty.

Other `@Query` options include `retColumns`, `distinct`, `orderBy`, `groupBy`, `condition`, and `strategy`. For example:

```java
@Query(orderBy = @OrderBy(value = "id", type = OrderBy.Type.DESC))
public abstract List<PersonData> newest(@Limit int limit);

@Query("SELECT COUNT(*) FROM {NAME} WHERE {M:name} LIKE {V:0}")
public abstract int countMatching(String pattern);
```

The second example returns a scalar count instead of a `PersonData`. `{V:0}` binds the first method argument; it is useful when you control the SQL yourself.

#### Return DTOs without registering another table

A query method can return a custom read-only result type that has no table or view of its own. Let the class implement `DatabaseEntry.ReadOnlyDatabaseEntry` and annotate the fields that match the SQL result column labels:

```java
public class PersonSummary implements ReadOnlyDatabaseEntry {
    @Column public String name;
    @Column public String note;

    public PersonSummary() {}
}

// Query method declared on a PersonTable repository:
@Query("SELECT {M:name} AS {Q:name}, {M:note} AS {Q:note} FROM {NAME}")
public abstract List<PersonSummary> summaries();
```

Here, PCLib reads `name` and `note` from the query result and creates `PersonSummary` objects. During query scanning it creates a **synthetic SQLQueryable** for the DTO's column mapping. This is internal metadata, not a physical SQL table or view: do not register `PersonSummary` with a `DatabaseTable`, and do not try to create or write to the synthetic queryable.

The SQL result column names must match the DTO's `@Column` names (or their `name` overrides). A normal `DatabaseEntry` returned from a query must belong to the queried table or a joined table. `ReadOnlyDatabaseEntry` is the option for arbitrary projections, such as reports that combine columns from several sources.

#### Join tables through foreign keys

`@Query(tables = { ... })` adds joined tables to a generated query. If you leave out `on`, PCLib looks for a foreign key between the new table and the main table or a previously declared join. A foreign key may point in either direction.

Suppose these table classes are also registered with the database:

```java
public class CarData implements DatabaseEntry {
    @Column @PrimaryKey @AutoIncrement public long id;
    @Column @ForeignKey(table = PersonTable.class) public long personId;
    @Column public @MaxLength(80) String brand;
    public CarData() {}
}

public class CarTable extends DatabaseTable<CarData> {
    public CarTable(Database database) { super(database); }
}

public class GarageData implements DatabaseEntry {
    @Column @PrimaryKey @AutoIncrement public long id;
    @Column @ForeignKey(table = CarTable.class) public long carId;
    public GarageData() {}
}

public class GarageTable extends DatabaseTable<GarageData> {
    public GarageTable(Database database) { super(database); }
}
```

The relationships are `CarData.personId -> PersonData.id` and `GarageData.carId -> CarData.id`. On a query of `PersonTable`, you can join `CarTable` without writing the `ON` expression:

```java
// Query methods declared on a PersonTable repository:
@Query(tables = { @Table(typeName = CarTable.class, join = Table.Type.INNER) })
public abstract List<PersonData> byCarBrand(@Param(member = "CarTable:brand") String brand);

// GarageTable is not in @Query(tables), but it has a FK to the joined CarTable.
@Query(tables = { @Table(typeName = CarTable.class, join = Table.Type.INNER) })
public abstract List<PersonData> byGarage(@Param GarageData garage);
```

The first query joins `person` to `car` on `person.id = car.person_id`, then filters on `car.brand`. `@Param(member = "CarTable:brand")` identifies a column on the joined table.

The second query shows **one-hop parameter resolution**. PCLib can use `GarageData` because `GarageTable` has a foreign key to the joined `CarTable`. It reads the garage's `carId` and filters on the car's primary key. `GarageTable` does **not** need to appear as another SQL `JOIN` for this filter. PCLib checks tables one foreign-key step away from the query's declared tables when resolving entry parameters; it does not traverse an unlimited chain of relations.

When there is no foreign-key path, or more than one possible path, specify the join condition yourself:

```java
@Query(tables = {
    @Table(typeName = CarTable.class, join = Table.Type.INNER,
           on = "{M:PersonTable:id} = {M:CarTable:personId}")
})
public abstract List<PersonData> byCarBrandExplicit(@Param(member = "CarTable:brand") String brand);
```

Use `@Table(asName = "c")` to add an SQL alias when needed, and `@Table(name = "...")` to select a specific registered table when a class has several instances. PCLib supports `INNER`, `LEFT`, `RIGHT`, `FULL`, and `CROSS` join types, subject to the database provider.

#### Build a view with annotations

`@View` defines a read-only SQL view from registered tables. Each `@Table` names a source, its join type, and the columns it contributes. `@SelectColumn` maps a source column to a field of the view's entry type.

This view combines each person's name with the brand of their cars. It uses the `PersonTable` and `CarTable` classes from the examples above:

```java
public class PersonCarRow implements ReadOnlyDatabaseEntry {
    @Column public long personId;
    @Column public String personName;
    @Column public String carBrand;
    public PersonCarRow() {}
}

@View(tables = {
    @Table(typeName = PersonTable.class, asName = "p", columns = {
        @SelectColumn(name = "id", asName = "{M:personId}"),
        @SelectColumn(name = "name", asName = "{M:personName}")
    }),
    @Table(typeName = CarTable.class, join = Table.Type.INNER, asName = "c", columns = {
        @SelectColumn(name = "brand", asName = "{M:carBrand}")
    })
})
public class PersonCarView extends DatabaseView<PersonCarRow> {
    public PersonCarView(Database database) { super(database); }
}
```

Register and scan the source tables before creating the view:

```java
PersonTable people = new PersonTable(database);
CarTable cars = new CarTable(database);
PersonCarView personCars = new PersonCarView(database);

database.register(people, cars, personCars).scanFromBeans();
database.createBeans((object, created) -> {});

int joinedRows = personCars.count();
```

The `@View` annotation controls the SQL definition. `@Table(join = ...)` selects the join type, `asName` sets an alias, and `@SelectColumn(asName = ...)` names the resulting column. As with query joins, PCLib resolves a missing `on` from foreign keys and requires an explicit condition if the relationship is ambiguous. `@View` also supports `condition`, `groupBy`, `orderBy`, `unionTables`, `with`, and `customSQL`. See the [view tests](../pclib-db-all/src/test/java/test/DBViewTest.java) for longer joins and examples with four source tables.

### SQL placeholders

Placeholders are resolved against the scanned table/view and its database. They work in `@Query` SQL, query conditions, and supported SQL expressions such as `@DefaultValue`. Some placeholders only make sense in a query method or a column expression.

| Placeholder | Resolves to | Example/notes |
| --- | --- | --- |
| `{NAME}` | Qualified name of the current table/view | `SELECT * FROM {NAME}` |
| `{FIELD}` | Qualified name of the current column | In a field-level `@Check`, `@DefaultValue`, or update expression |
| `{Q:name}` | DBMS-quoted SQL identifier | MySQL: `` `name` ``; PostgreSQL/SQLite: `"name"` |
| `{F:name}` | Name of a DBMS-specific SQL function | `{F:count}`, `{F:current_timestamp}`; names depend on the provider |
| `{T:TableClass}` | Qualified name of another registered table/view | `{T:FrenchPersonTable}` or `{T:definedName}` |
| `{T:TableClass:tableName}` | A table with the given class and resolved name | Distinguishes multiple tables using one class |
| `{M:field}` | Qualified column for a field in the current entry | `{M:name}` |
| `{M:TableClass:field}` | Qualified column in another registered table/view | Also accepts a `@DefinedName` alias |
| `{M:TableClass:tableName:field}` | Column in a specific instance of that table class | Useful when one class represents multiple tables |
| `{A:TableClass}` / `{A:TableClass:tableName}` | Join alias of another table | Used in generated query conditions that declare joined tables |
| `{P:param}` | Column associated with a query parameter | `{P:name}`; `param` is its name or zero-based index |
| `{V:param}` | A bound parameter placeholder (`?`) | `{V:name}`; also records JDBC binding order |
| `{E:key}` | Property value: current queryable hints, then Java system properties, then environment | `{E:DEFAULT_COUNTRY}` |
| `{E.queryable:key}` | Hint from the current table/view only | `{E.queryable:DEFAULT_COUNTRY}` |
| `{E.db:key}` | Hint from the current database only | `{E.db:DB_CHARACTER_SET}` |
| `{E.prop:key}` | Java system property (`System.getProperty`) | `{E.prop:user.timezone}` |
| `{E.env:key}` | Operating-system environment variable | `{E.env:APP_REGION}` |

Property placeholders support a fallback within the braces: `{E:key:fallback}`, `{E.db:key:fallback}`, `{E.env:key:fallback}`, and so on. Without a matching value or fallback, they throw a `PropertyNotFoundException`.

For queries, use `{V:...}` for values so JDBC binds them as parameters. Use `{Q:...}` or `{M:...}` for identifiers; these cannot be replaced by `?` parameters. This is only useful if you have query expressions where you want to reuse the same parameter multiple times or re-order the condition order to not match the parameter order.

```java
@Query("SELECT * FROM {NAME} WHERE {M:name} = {V:name}")
public PersonData byName(@Param("name") String name) {
    return byNameFunction.get(name);
}
```

`{V:name}` becomes `?` and binds the method's `name` argument. Names require Java parameter-name retention; a zero-based index such as `{V:0}` also works. The full Spring proxy example is in the [Spring guide](../pclib-db-spring/#query-methods).

### Use `@Query` at runtime without Spring

Use `BaseProxyDatabaseEntryUtils` rather than the basic `BaseDatabaseEntryUtils`. It implements `ProxyDatabaseEntryUtils` and exposes a `QueryFunctionProvider`, which builds a callable `ParamFunction` from a query method.

```java
public class PersonTable extends DatabaseTable<PersonData> {

    @QueryFunction(methodName = "byName")
    private ParamFunction byNameFunction;

    public PersonTable(Database database) {
        super(database);
    }

    @Query("SELECT * FROM {NAME} WHERE {M:name} = {V:0}")
    public PersonData byName(@Param("name") String name) {
        return byNameFunction.get(name);
    }

}
```

The scanner finds the named method, builds its query function, and assigns it to `byNameFunction`. This happens during `scanFromBeans()`; call the method **after** scanning.
Alternatively, put `@QueryFunction(fieldName = "byNameFunction")` on the method.

```java
ProxyDatabaseEntryUtils utils = new BaseProxyDatabaseEntryUtils("sqlite");
Database database = new Database(new SQLiteDatabaseConnector("./data"), "people.sqlite", utils);

PersonTable people = new PersonTable(database);
database.register(people).scanFromBeans();
database.createBeans((object, created) -> {});

people.insertAndReload(new PersonData("Alice"));
PersonData alice = people.byName("Alice");
```

You can also build a function explicitly, without `@QueryFunction` field injection:

```java
Method method = PersonTable.class.getDeclaredMethod("byName", String.class);
ParamFunction function = utils.getQueryFunctionProvider().buildMethodQueryFunction(people, method);
PersonData alice = function.get("Alice");
```

`ParamFunction.get(Object...)` calls the query with the supplied arguments and casts its result. `QueryFunctionProvider` also exposes `buildMethodQueryStructure(...)` when you need to inspect the generated SQL. The [`DatabaseBuilder`](#build-tables-at-runtime-with-databasebuilder) can connect query methods to `ParamFunction` consumers through `TablePlan.query(...)`.

## Supported Java column types

The built-in MySQL, SQLite, and PostgreSQL column registries cover these types:

| Category | Java types |
| --- | --- |
| Text | `String`, `CharSequence`, `char`, `Character`, `char[]`, `UUID`, enums |
| Integer | `byte`, `short`, `int`, `long` and boxed variants; `BigInteger` |
| Decimal | `float`, `double` and boxed variants; `BigDecimal`; `Number` with precision/scale hints |
| Boolean | `boolean`, `Boolean` |
| Binary | `byte[]`, `ByteBuffer` |
| Date/time | `java.sql.Date`, `Time`, `Timestamp`; `java.util.Date`; `Instant`, `LocalDate`, `LocalTime`, `LocalDateTime`, `OffsetTime`, `OffsetDateTime`, `ZonedDateTime`, `Duration`, `Period`, `MonthDay`, `Year`, `YearMonth` |
| JSON | `org.json.JSONObject`, `org.json.JSONArray` when the JSON classes are available |

PostgreSQL additionally registers `String[]` and `int[]` arrays (including multidimensional arrays). Alternate enum, `MonthDay`, and `YearMonth` mappings exist as provider-specific column types. SQL storage formats and exact supported hints depend on the provider.

With [`pclib-db-spring`](../pclib-db-spring/), Jackson adds JSON-backed `List`/`ArrayList`/`LinkedList` and `Map`/`HashMap`/`LinkedHashMap`/`TreeMap` mappings.

## Provider-specific hints

Some hints only apply to a particular DBMS:

| Provider | Setting | How to set it |
| --- | --- | --- |
| MySQL | String/binary size class | `@Size(SizeClass.MEDIUM)` (other values: `TINY`, `SMALL`, `NORMAL`, `LONG`) |
| MySQL | Character set, collation, engine | `@CharacterSet`, `@Collation`, `@Engine` |
| PostgreSQL | Table schema | Core `@Schema("public")` on the table or database |
| PostgreSQL | Database locale | `@LcCollate`, `@LcCType` on the table or database |
| PostgreSQL | Materialized view | `@Materialized(true)` on a view |
| SQLite | Connection/database PRAGMAs | `@ForeignKeys(true)`, `@CacheSize`, `@BusyTimeout`, `@AutoVaccum`, `@ApplicationId`, `@JournalMode`, `@RecursiveTriggers`, `@SecureDelete`, `@Synchronous`, `@TempStore`, `@UserVersion` |

You can also set the hints at runtime using the database's custom hints:

```java
database.getCustomHints().put(SQLiteDatabaseHints.FOREIGN_KEYS, true);
database.getCustomHints().put(SQLiteDatabaseHints.BUSY_TIMEOUT, 5000);
```

## Transactions

Transactions bind table calls to one connection. Closing without committing rolls back:

```java
try (DBTransaction tx = database.createTransaction()) {
  tx.use(people).insertAndReload(new PersonData("Bob"));
  tx.commit();
}
```

Use `pclib-db-spring` to be able to use `@Query` methods in transactions.

## SQL queryable hooks and rules

`SQLQueryableHookManager` runs `SQLQueryableRule` callbacks around table, view, and query operations. Rules can prepare an operation, inspect it before execution, act during it, handle its result, or react to an error.

| Stage | Interface | Example hook |
| --- | --- | --- |
| Prepare | `SQLQueryableRule.PrepareRule` | `PREPARE_UPDATE` |
| Before | `SQLQueryableRule.BeforeRule` | `BEFORE_INSERT` |
| During | `SQLQueryableRule.DuringRule` | `DURING_LOAD` |
| After | `SQLQueryableRule.AfterRule` | `AFTER_UPDATE` |
| Error | `SQLQueryableRule.ErrorRule` | `ERROR_QUERY` |

Each rule implements `shouldRun(RuleHookType, SQLQueryable<?>)` to select the operations it handles. The nested interfaces `InsertRule`, `LoadRule`, `UpdateRule`, and `QueryRule` provide filters for those operation types. A rule can implement more than one stage or operation interface.

The manager on `DatabaseEntryUtils` is the parent of the managers created for its tables and views. Rules added to the parent propagate to linked children. You can also add a rule to one table without affecting the others:

```java
SQLQueryableHookManager globalHooks = database.getDatabaseEntryUtils().getQueryableHookManager();
globalHooks.add(new VersionRule());

// Register a rule for this table only.
people.getQueryableHookManager().add(new InsertLogRule());
```

### Version checking with `VersionRule`

Add a version column to an entry class and register `VersionRule` to detect updates made since an object was loaded:

```java
public class PersonData implements DatabaseEntry {
    @Column @PrimaryKey @AutoIncrement public long id;
    @Column public String name;
    @Column @Version public int version; // Defaults to 0; increments in SQL on update.
    public PersonData() {}
}

database.getDatabaseEntryUtils().getQueryableHookManager().add(new VersionRule());

PersonData current = people.load(existingPerson);
PersonData stale = people.load(existingPerson);
current.name = "New name";
people.updateAndReload(current);

// Throws VersionConflictException: stale.version no longer matches the row.
people.updateAndReload(stale);
```

`VersionRule` checks the current database version before an update and rejects stale entries with `VersionConflictException`. The `@Version` annotation defines a default value and update expression; `@TimeVersion` uses a timestamp instead. Registering the rule is necessary: the annotations alone do not perform the conflict check. The default `VersionRule()` covers single-entry updates; `new VersionRule(true)` also checks batch updates. In both cases, load the updated entry again (for example through `updateAndReload`) to refresh its version.

### Write a custom rule

This rule logs insert operations before they run:

```java
public class InsertLogRule implements SQLQueryableRule.BeforeRule {

    @Override
    public boolean shouldRun(RuleHookType type, SQLQueryable<?> queryable) {
        return type == RuleHookType.BEFORE_INSERT;
    }

    @Override
    public void executeBefore(RuleHookType type, SQLQueryable<?> queryable,
                              AbstractConnection connection, Statement statement, Object data) {
        System.out.println("Inserting into " + queryable.getName());
    }
}

people.getQueryableHookManager().add(new InsertLogRule());
```

The arguments expose the queryable, connection, statement, and operation data. Keep a rule's work focused on the selected hook: an exception from a callback can interrupt the database operation. Use `remove(rule)` to unregister a rule and `addBefore(...)` / `addAfter(...)` to control its order relative to another rule class. See [the hook manager tests](../pclib-db-all/src/test/java/QueryableHookManagerTest.java) for rule propagation and ordering.

## GraalVM Native Image

The standalone core module relies on reflection and runtime query/model scanning. It is **not supported by itself** in a GraalVM native image. Use [`pclib-db-spring`](../pclib-db-spring/#graalvm-native-image), which provides Spring AOT processing and generated queryable classes. The *application* must run Spring Boot's `process-aot` step before native compilation; compiling this library alone does not replace that step.

For a complete Spring example, see [pclib-db-sample](https://github.com/UnKabaraQuiDev/pclib-db-sample).
