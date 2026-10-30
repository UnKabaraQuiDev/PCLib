# PCLib DB Spring

`pclib-db-spring` integrates the [PCLib database framework](../pclib-db-core/) with Spring Boot. It creates database beans from configuration, discovers table and view components/beans, implements abstract `@Query` methods, and manages schema creation and migrations.

**Requirements:** Java 17 and Spring Boot 4.x. For a complete application, see [pclib-db-sample](https://github.com/UnKabaraQuiDev/pclib-db-sample).

## Dependencies

Add the Spring module **and** a database provider. The Spring module does not bundle one:

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-db-spring</artifactId>
</dependency>
```

Use [`pclib-db-mysql`](../pclib-db-mysql/), [`pclib-db-postgres`](../pclib-db-postgres/), or [`pclib-db-all`](../pclib-db-all/) as needed.

## Configure databases

Place named connector sections under `pclib.db` in `application.yml`:

```yaml
pclib:
  db:
    enabled: true
    auto-create: true
    auto-migrate: true

    main:
      protocol: mysql
      name: app_db
      host: localhost
      port: 3306
      username: ${DB_USER}
      password: ${DB_PASSWORD}
      hints:
        DB_CHARACTER_SET: utf8mb4
        DB_COLLATION: utf8mb4_general_ci

    reporting:
      protocol: postgresql
      name: reports
      host: localhost
      port: 5432
      username: ${PG_USER}
      password: ${PG_PASSWORD}

    local:
      protocol: sqlite
      name: local.sqlite
      dir-path: ./data
      hints:
        FOREIGN_KEYS: true
        JOURNAL_MODE: WAL
        BUSY_TIMEOUT: 5000
```

Each section's name is its bean qualifier. You can override it with `qualifier: otherName`. `name` sets the database name (or SQLite file name) and defaults to the qualifier.

### Protocol-specific configuration

| Module | `protocol` | Connector properties | Other settings |
| --- | --- | --- | --- |
| [`pclib-db-mysql`](../pclib-db-mysql/) | `mysql` | `username`, `password`, `host` (default `localhost`), `port` (default `3306`) | `DB_CHARACTER_SET`, `DB_COLLATION`, `DB_ENGINE` (see `DefaultDatabaseHints`) |
| [`pclib-db-sqlite`](../pclib-db-sqlite/) | `sqlite` | `dir-path` (default `.`) | SQLite PRAGMAs through `hints`, e.g. `FOREIGN_KEYS`, `JOURNAL_MODE`, `SYNCHRONOUS`, `BUSY_TIMEOUT`, `CACHE_SIZE` (see `DefaultDatabaseHints`, `SQLiteDatabaseHints`) |
| [`pclib-db-postgres`](../pclib-db-postgres/) | `postgresql` | `username`, `password`, `host` (default `localhost`), `port` (default `5432`) | `LC_COLLATE`,  `LC_CTYPE` (see `DefaultDatabaseHints`, `PostgreSQLQueryableHints`, `PostgreSQLDatabaseHints`) |

Property names accept hyphenated and camel-case forms (`dir-path` / `dirPath`). `postgresql` is the registered provider name; do not shorten it to `postgres` in this configuration.

There are two separate kinds of settings:

- **Connector properties** (`host`, `port`, `username`, `password`, `dir-path`) go to the provider's connector factory.
- **Database hints** (`hints:`) become entries in the database's hint map. See the [core guide](../pclib-db-core/#what-hints-are) for scopes and annotation equivalents.

### Global defaults and per-connector overrides

| Option | Default | Behavior |
| --- | :---: | --- |
| `enabled` | `true` | Enables the auto-configuration |
| `expose-connector` | `true` | Exploses the `Databaseconnector` beans |
| `expose-database` | `true` | Registers the `DeferredDatabase` beans |
| `auto-create` | `true` | Creates missing databases, tables, and views at startup |
| `auto-migrate` | `true` | Applies available migration phases at startup |
| `auto-add-columns` | `true` | Allows automatic column additions during migration |
| `auto-remove-columns` | `false` | Allows column removal during migration |
| `auto-remove-tables` | `false` | Allows table removal during migration |
| `migration-schema-name` | `__db_schema` | Names the internal migration tracking table |
| `print-created` | `true` | Logs database creation |
| `print-created-table` | `true` | Logs table creation |
| `print-created-view` | `false` | Logs view creation |
| `print-deleted` | `true` | Logs deletion |
| `print-existed` | `false` | Logs tables or views that existed |
| `print-migration-applied` | `true` | Logs applied migration phases |

The options apart from `enabled` can also be set per connector. A connector-specific value takes priority over the global one:

```yaml
pclib:
  db:
    auto-create: true # default
    auto-remove-columns: false

    archive:
      protocol: sqlite
      name: archive.sqlite
      dir-path: ./data
      auto-create: false  # The application manages this schema itself.
```

Set `auto-migrate: false` when you only want missing tables and views to be created without any schema changes throughout the versions (may cause issues if the structure of the table changes). `auto-create: false` skips automatic creation and migration altogether for that connector. Review removal options before enabling them against an existing database.

## Spring beans and injection

For each connector, Spring creates these names:

| Section | Database bean | Connector-factory bean | Entry-utils bean |
| --- | --- | --- | --- |
| `main` | `main` | `mainConnector` | `mainDatabaseEntryUtils` |
| `reporting` | `reporting` | `reportingConnector` | `reportingDatabaseEntryUtils` |
| `local` | `local` | `localConnector` | `localDatabaseEntryUtils` |

The database bean is a `DeferredDatabase` (which extends `Database`). The connector bean is a `DatabaseConnectorFactory`; the entry-utils bean is a `DatabaseEntryUtils`. `expose-connector: false` and `expose-database: false` suppress their respective beans, you'll need to define them manually.

With multiple databases, use `@Qualifier`:

```java
@Component
public abstract class PersonTable extends DeferredDatabaseTable<PersonData> {

  protected PersonTable(@Qualifier("main") DeferredDatabase database) {
    super(database);
  }
}
```

Define your data fields in a `DatabaseEntry` implementation:

```java
public class PersonData implements DatabaseEntry {

  @Column @PrimaryKey @AutoIncrement
  public long id;

  @Column @Unique
  public @MaxLength(80) String name;

  public PersonData() {}

}
```

See the [core guide](../pclib-db-core/#hint-scopes-and-hierarchy) for type hints (`@MaxLength`, `@DecimalParam`), column hints (`@Unique`, `@Index`, `@Version`), and table/view hints (`@Schema`, `@Engine`).

## Query methods

`DeferredDatabaseTable` and `DeferredDatabaseView` allow abstract query methods. Spring supplies the implementation:

```java
@Component
public 
  abstract // THE CLASS HAS TO BE ABSTRACT !!
  class PersonTable extends DeferredDatabaseTable<PersonData> {

  protected PersonTable(@Qualifier("main") DeferredDatabase database) {
    super(database);
  }

  @Query(columns = { "name" })
  public abstract Optional<PersonData> byName(String name);

  @Query
  public abstract List<PersonData> search(
      @Param(value = "name", comparator = "LIKE", ignoreNull = true) String pattern,
      @Limit int limit,
      @Offset int offset);

  @Query("SELECT * FROM {NAME} WHERE {M:name} = ?")
  public abstract List<PersonData> byExactName(String name);

}
```

Because these are actual classes, you can define fields and other logic directly in the repository.

`@Query` accepts raw SQL in `value`, selected `columns`, or generated conditions from `@Param`. `ignoreNull` skips a condition when its parameter is `null`. Query parameters can also use `@All`, `@Any`, `@NotNull`, `@OrIsNull`, `@Limit`, and `@Offset`. `@Query` has strategies for first/single/list results; the default is `AUTO`.

### Choose a table or view type

Spring generates the implementation for an abstract repository class or a repository interface annotated with `@Component`. The available forms differ in how much code you write yourself:

| Type | Form | Use when |
| --- | --- | --- |
| `DeferredDatabaseTable<T>` | Abstract class | You need `@Query` methods alongside fields, constructors, or normal Java methods. |
| `ProxyDatabaseTable<T>` | Interface | You only need a table API and query methods; it extends `AbstractDBTable<T>`. |
| `AbstractDBTable<T>` | Interface | You need the table API without extending `ProxyDatabaseTable<T>`; Spring can proxy this interface too. |
| `DeferredDatabaseView<T>` | Abstract class | You want a read-only view repository with custom logic. |
| `ProxyDatabaseView<T>` | Interface | You want a view API and query methods; it extends `AbstractDBView<T>`. |
| `AbstractDBView<T>` | Interface | You need the view API without extending `ProxyDatabaseView<T>`. |

For an abstract class, define a constructor that takes `DeferredDatabase` and passes it to `super`. Use `@Qualifier` when you have more than one database. `DeferredDatabaseTable` and `DeferredDatabaseView` also accept an optional table or view name as the second constructor argument, which you supply from your own constructor:

```java
@Component
public abstract class FrenchPersonTable extends DeferredDatabaseTable<PersonData> {

  protected FrenchPersonTable(@Qualifier("main") DeferredDatabase database) {
    super(database, "french_people");
  }

  @Query(columns = { "name" })
  public abstract Optional<PersonData> byName(String name);
}
```

For an interface, there is no Java constructor. Declare a method named `ctor` to tell PCLib which underlying constructor to call and where to get its arguments:

```java
@Component
public interface PersonLookupTable extends ProxyDatabaseTable<PersonData> {

  @Override
  void ctor(@Qualifier("main") DeferredDatabase database);

  @Query(columns = { "name" })
  Optional<PersonData> byName(String name);
}
```

The `ctor` method is a **constructor declaration**, not a method you call. The factory reads its parameter types and annotations, then injects the arguments into the generated table or view. `ProxyDatabaseTable` and `ProxyDatabaseView` declare `ctor(DeferredDatabase)` already; redeclare it to add `@Qualifier`. A plain `AbstractDBTable` or `AbstractDBView` interface may declare the same `ctor` method for this purpose. Keep its parameters compatible with the underlying `DeferredDatabaseTable` or `DeferredDatabaseView` constructors: `DeferredDatabase`, or `DeferredDatabase` plus `String` for a name. Any extra constructor parameter also needs a Spring-resolvable value.

If an interface omits `ctor`, the factory chooses an available underlying constructor. Define `ctor` explicitly when you need predictable constructor selection, especially with multiple databases. Interface repositories cannot hold instance fields or method implementations; use a deferred abstract class for those cases.

### What happens at startup

When `auto-create` is enabled, `DatabaseInitializer` scans the Spring table/view beans for each database, initializes the schema and migration support, creates the database if needed, and creates or migrates its tables and views. Migration options control whether columns or tables may be removed. Avoid mixing manual schema changes with automatic migration without reviewing the resulting migration plan.

### Register manual migration stages

Implement `DatabaseMigration` and register it as a Spring bean (`@Component` or `@Bean`). `DatabaseInitializer` collects these beans when `auto-create` and `auto-migrate` are enabled. A migration contains one or more phases that run alongside automatic schema changes.

This example cleans up existing names after schema updates:

```java
@Component
public class NormalizePersonNames implements DatabaseMigration {

  private final DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
        new ManualMigrationPhase("trim-names", "Remove surrounding spaces", 0, MigrationPhase.MANUAL) {
          @Override
          public void up(Statement stmt) throws SQLException {
            stmt.executeUpdate("UPDATE person SET name = TRIM(name) WHERE name IS NOT NULL");
          }
        }
    };

  @Override
  public String id() {
    return "normalize-person-names"; // Keep this ID unchanged once released.
  }

  @Override
  public String name() {
    return "Normalize person names";
  }

  @Override
  public int order() {
    return 10; // Lower migration orders run first.
  }

  @Override
  public DatabaseMigrationPhase[] phases() {
    return phases;
  }
}
```

| Setting | Meaning |
| --- | --- |
| Migration `id()` | Stable identifier recorded in migration history. Do not rename it after deployment. |
| Migration `order()` | Order among registered migrations; smaller numbers run first. |
| Phase `id` | Stable identifier unique within its migration. A completed phase is not run again. |
| Phase `order` | Order among phases of the same `MigrationPhase`. |
| `MigrationPhase.MANUAL` | Runs after the automatic schema phases (the default manual stage). |
| `MigrationPhase.ADD_COLUMNS` | Runs after PCLib adds columns and before later schema phases. Useful for filling a new column before adding constraints. |

Other phases include `FIRST`, `ADD_TABLE`, `RENAME_TABLES`, `RENAME_COLUMNS`, `CHANGE_COLUMNS`, `ADD_CONSTRAINTS`, and `REMOVE_TABLES`. They follow the order of the `MigrationPhase` enum. PCLib runs each manual phase at the selected stage and records its completion in the migration history tables.

On a fresh database, PCLib creates the current schema and records the supplied migration phases as already applied. Manual phases are intended to change **existing** database contents during an upgrade, not to seed every new database. If you have multiple configured databases, note that the initializer passes **all** `DatabaseMigration` beans to each one; ensure the SQL applies to every target database or arrange separate registration.

See the [Spring migration tests](src/test/java/lu/kbra/pclib/db/migration/) for multi-phase examples.

## Supported types and custom types

The built-in provider registries support strings, numbers, booleans, enums, UUIDs, binary data, Java date/time classes, and `org.json` values. PostgreSQL also supports `String[]` and `int[]` column mappings. The full [type table](../pclib-db-core/#supported-java-column-types) lists the Java classes.

Spring adds Jackson-backed mappings for:

- `List`, `ArrayList`, `LinkedList`
- `Map`, `HashMap`, `LinkedHashMap`, `TreeMap`

To register a custom type, implement `DatabaseColumnTypeFactory` as a Spring component. `registerColumnTypes(List<ColumnTypeFactory<?>>)` adds its mappings; `matches(String protocol)` limits them to a DBMS. The factory can inject an `ObjectMapper` or other Spring beans for serialization. Mappings are ranked by match score, so a type-hint-specific mapping can take priority over a fallback.

## Create repository beans with `QueryableTemplate`

`QueryableTemplate` lets you create another Spring bean from an existing table or view class while changing its table name and hints. This is useful when the same repository class is used for multiple tables, such as current and archived data.

For example, suppose `PersonTable` is the abstract `DeferredDatabaseTable<PersonData>` class shown above. Add a template bean:

```java
@Configuration
public class RepositoryTemplates {

  @Bean
  QueryableTemplate archivedPeople() {
    return new QueryableTemplate(PersonTable.class)
        .setName("archived_people")
        .setTableId("archived-people")
        .setDefinedName("archive");
  }
}
```

PCLib registers a `PersonTable` repository bean named `archivedPeople`. It renames the original template definition to `archivedPeopleTemplate`. You can inject the generated repository like any other bean:

```java
@Service
public class ArchiveService {
  private final PersonTable archivedPeople;

  public ArchiveService(@Qualifier("archivedPeople") PersonTable archivedPeople) {
    this.archivedPeople = archivedPeople;
  }
}
```

| Template method | Purpose |
| --- | --- |
| `setName("archived_people")` | Overrides the physical table or view name. |
| `setTableId("archived-people")` | Sets the stable table ID used for migration tracking. |
| `setDefinedName("archive")` | Defines an alternate name for resolving the repository in SQL placeholders. |
| `set(key, value)` | Adds a hint. |
| `setTargetClass(type)` | Changes the repository type instantiated by the template. |

The repository class must have a constructor that Spring can resolve, including its `@Qualifier` if several `DeferredDatabase` beans exist. A concrete `DatabaseTable` or `DatabaseView` class can also be used; PCLib calls its constructor instead of generating a subclass. The template's hints are copied into the repository before database scanning.

## Validate entries with `ValidationRule`

`ValidationRule` uses Jakarta Bean Validation and Hibernate Validator to check entries before **insert** and **update** operations. It creates a validator from each scanned table structure. This lets PCLib enforce some column hints in Java before sending SQL.

By default, it handles @Nullable, @NotNull, @FixedLength and @MaxLength, but the other Validation annotations like @Pattern, @Email, etc are also supported.

The rule is **not enabled just by adding `pclib-db-spring`**. Add it to a `DatabaseQueryableHookTemplate`, and ensure Hibernate Validator and the desired `ConstraintCreator` beans are available:

```java
@Configuration
public class ValidationConfiguration {

  @Bean
  ValidationRule validationRule(TableValidatorFactory factory) {
    return new ValidationRule(factory);
  }

  @Bean
  DatabaseQueryableHookTemplate validationHooks(ValidationRule rule) {
    return new DatabaseQueryableHookTemplate().add(rule);
  }
}
```

The Spring module provides `TableValidatorFactory` when Jakarta Validation is present. If validation fails, the rule throws `ConstraintViolationException` and the database operation does not proceed.

Use the `@SkipValidation` queryable annotation for a table that must bypass this rule, or set `ValidationRule.SKIP_VALIDATION` to `true` in that table's custom hints before scanning:

```java
@SkipValidation
@Component
public abstract class ImportTable extends DeferredDatabaseTable<ImportData> {
  protected ImportTable(@Qualifier("main") DeferredDatabase database) {
    super(database);
  }
}
```

`ValidationRule` validates ordinary field-backed table entries and collections used in batch operations. For custom `StorageBinding` implementations, check that `TableValidatorFactory` supports the binding before enabling this rule. You can also create your own `ConstraintCreator` to add constraints from other column hints. See [core hooks and `VersionRule`](../pclib-db-core/#sql-queryable-hooks-and-rules) for other rules and the execution stages.

## GraalVM Native Image

PCLib's Spring integration supports GraalVM through Spring AOT (ahead-of-time processing). Its `SQLQueryableAotProcessor` generates implementations of abstract table and view components and registers constructor reflection metadata. The module also registers Spring runtime hints.

**Spring Boot's `process-aot` goal is required.** A direct GraalVM `native:compile` invocation without the Spring AOT phase does not prepare PCLib's query proxies. Run AOT in the *application module* that has your `@SpringBootApplication` entry point, not in `pclib-db-core` or in the library-only `pclib-db-spring` module.

For Maven, one option is to add a `native` profile to the Spring Boot application's `pom.xml`:

```xml
<profile>
  <id>native</id>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>4.1.2</version>
        <executions>
          <execution>
            <id>process-aot</id>
            <phase>process-classes</phase>
            <goals><goal>process-aot</goal></goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.graalvm.buildtools</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <version>1.1.17</version>
        <extensions>true</extensions>
        <executions>
          <execution>
            <id>compile-native</id>
            <phase>package</phase>
            <goals><goal>compile-no-fork</goal></goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</profile>
```

With GraalVM and `native-image` installed, run:

```bash
./mvnw -Pnative -DskipTests package
```

Maven compiles the application, runs `spring-boot:process-aot`, then builds the native executable. The application must provide a discoverable main class. If Spring reports *Unable to find a suitable main class*, set `<mainClass>your.package.Application</mainClass>` in the Spring Boot plugin configuration; a library module without an application entry point cannot run this goal on its own.

If you use a different native Maven profile, bind `spring-boot:process-aot` before native compilation there as well. Make sure your provider's JDBC driver and any custom reflection/serialization code also work in the native executable. PCLib's AOT support does not automatically cover arbitrary third-party libraries or custom types.
