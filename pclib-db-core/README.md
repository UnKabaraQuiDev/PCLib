# PCLib DB

`pclib-db` is the database module of PCLib. It provides annotation-based table and view mapping, SQL builders, database connectors, transactions, query helpers, and DBMS-specific SQL generation.

**This module has better support using pclib-db-spring, which includes automated creation of tables/views as well as query and transaction proxies**

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-db</artifactId>
</dependency>
```

## Supported DBMS

`pclib-db` uses provider-based DBMS support. A DBMS provider registers the connector support, column type registry, and SQL structure visitor for one protocol.

Already implemented protocols:

| Protocol | Connector | Type registry | Structure visitor |
|---|---|---|---|
| [../pclib-db-mysql/](`mysql`) | `MySQLDataBaseConnector` | `MySQLColumnTypeRegistry` | `MySQLStructureVisitor` |
| [../pclib-db-sqlite/](`sqlite`) | `SQLiteDataBaseConnector` | `SQLiteColumnTypeRegistry` | `SQLiteStructureVisitor` |
| [../pclib-db-postgres/](`postgres`) | `PostgreSQLDataBaseConnector` | `PostgreSQLColumnTypeRegistry` | `PostgreSQLStructureVisitor` |

You can also register a provider manually:

```java
DbmsProviders.registerProvider(new MyCustomDbmsProvider());
```

## Main features

This module includes:

* annotation-based table scanning
* annotation-based view generation
* automatic column type mapping
* DBMS-specific SQL dialect visitors
* database creation and deletion helpers
* table creation and update helpers
* view creation helpers
* transactions through `DBTransaction`

## Connecting to a database
*You'll need to add the needed dependency for your protocol, see [#supported-dbms](Supported DBMS)*

### MySQL

```java
MySQLDataBaseConnector connector = new MySQLDataBaseConnector("user", "pass", "localhost", 3306);
DataBase db = new DataBase(connector, "app_db");

db.create();
```

### SQLite

```java
SQLiteDataBaseConnector connector = new SQLiteDataBaseConnector("./data");
DataBase db = new DataBase(connector, "app_db.sqlite");

db.create();
```

### PostgreSQL

```java
PostgreSQLDataBaseConnector connector = new PostgreSQLDataBaseConnector("user", "pass", "localhost", 5432);
DataBase db = new DataBase(connector, "app_db");

db.create();
```

```java
PostgreSQLDataBaseConnector connector = new PostgreSQLDataBaseConnector(
    "user",
    "pass",
    "localhost",
    "app_db",
    5432,
    "postgres"
);
```

## Data classes

```java
public class PersonData implements DataBaseEntry {

  @Column
  @AutoIncrement
  @PrimaryKey
  /*
   * @Column, @AutoIncrement, @PrimaryKey, @Unique, @ForeignKey, @Check, @OnUpdate, @Generated, @DefaultValue
   * are applied on the field, not the type.
   * @DefaultValue is repeatable, with a `dbms=""` parameter, specifying the dbms for which it should apply, given that different dialects have different functions.
   * @Check is also repeatable, you can use placeholders for {FIELD} and {TABLE} in the condition.
   */
  protected int id;

  @Column
  @Unique
  protected @MaxLength(30) String name;
  /*
   * @MaxLength, @DecimalParam, @FixedLength, @TimeOffset, @TimeZone, @TypeHint, @TypeOverride, ...
   * are type-hint annotations, they are meta-annotated with @TypeHint.
   * Depending on the type hints, another ColumnTypeFactory will be used.
   *
   * For example:
   * * String + @MaxLength(30) => VARCHAR(30)
   * * String + @FixedLength(30) => CHAR(30)
   * * String => TEXT
   */

  @Column
  protected Date birthDate;

  public PersonData() {
  }

  public PersonData(int id) {
    this.id = id;
  }

  public PersonData(String name, Date birthDate) {
    this.name = name;
    this.birthDate = birthDate;
  }

}
```

## Table classes

```java
public class PersonTable extends DataBaseTable<PersonData> {

  public PersonTable(DataBase dataBase) {
    super(dataBase);
  }

}
```

## Creating and using a table

```java
DataBase db = new DataBase(new MySQLDataBaseConnector("user", "pass", "localhost", 3306), "app_db");
db.create();

PersonTable people = new PersonTable(db);
people.create();

Date date = PCUtils.toDate(Timestamp.from(Instant.now()));
PersonData person = new PersonData("Alice", date);

// Insert the entry and reload generated values, such as primary keys.
people.insertAndReload(person);

// Delete by primary key.
people.delete(person);

// Delete by unique key.
people.deleteUnique(person);
```

## Transactions

`DBTransaction#use` creates a transaction-bound proxy for the given table. Calls through that proxy use the transaction connection.

```java
try (DBTransaction tx = db.createTransaction()) {
  tx.use(people).insertAndReload(person);

  assert tx.use(people).exists(person);
  assert !people.exists(person);

  tx.commit();
  // or:
  // tx.rollback();
}
```

`close()` rolls back by default if you do not commit.

## Views

Views can be built from annotated table definitions. If the `on` clause is omitted, it tries to find the join path from the structure.

```java
public class PersonCarROData implements ReadOnlyDataBaseEntry {

  @Column
  protected Integer personId;

  @Column
  protected String personName;

  @Column
  protected Integer carId;

  @Column
  protected String carBrand;

}

@DB_View(
  name = "person_car_view",
  tables = {
    @ViewTable(
      typeName = PersonTable.class,
      asName = "p",
      columns = {
        @ViewColumn(name = "id", asName = "person_id"),
        @ViewColumn(name = "name", asName = "person_name")
      }
    ),
    @ViewTable(
      typeName = CarTable.class,
      join = ViewTable.Type.INNER,
      asName = "c",
      columns = {
        @ViewColumn(name = "id", asName = "car_id"),
        @ViewColumn(name = "brand", asName = "car_brand")
      }
    )
  }
)
public class PersonCarView extends DataBaseView<PersonCarROData> {

  public PersonCarView(DataBase dataBase) {
    super(dataBase);
  }
  
}
```

If no join path is found, or if more than one path is possible, view generation throws an error and you'll need to define the `on` clause yourself.

## Column type registries

Column mappings are grouped by DBMS, they get loaded from the protocol given to `BaseDataBaseEntryUtils`:

```java
DataBaseEntryUtils utils = new BaseDataBaseEntryUtils("postgres");
```

Or you can pass a registry directly:

```java
DataBaseEntryUtils utils = new BaseDataBaseEntryUtils("postgres", new PostgreSQLColumnTypeRegistry());
```

## DBMS providers

To add another database backend, implement `DbmsProvider`:

```java
public final class MyDbmsProvider implements DbmsProvider {

  @Override
  public String getProtocol() {
    return "mydb";
  }

  @Override
  public ColumnTypeRegistry createColumnTypeRegistry() {
    return new MyColumnTypeRegistry();
  }

  @Override
  public SQLStructureVisitor createStructureVisitor(DataBaseConnector connector) {
    return new MyStructureVisitor(connector);
  }

  @Override
  public SQLQueryVisitor createQueryVisitor(DataBaseConnector connector) {
    return new MyQueryVisitor();
  }

  @Override
  public DataBaseConnectorFactory createConnectorFactory(Map<String, Object> properties) {
    MyDataBaseConnector connector = new MyDataBaseConnector();
    // read properties here
    return connector::clone;
  }
  
}
```

Then register it in `META-INF/services/lu.kbra.pclib.db.dbms.DbmsProvider`
