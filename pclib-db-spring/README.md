# PCLib DB Spring

`pclib-db-spring` adds Spring Boot support for `pclib-db`. It can create database connectors, `DeferredDataBase` beans, and DBMS-specific `DataBaseEntryUtils` beans from `application.yml`.

**Java version:** Java 17  

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-db-spring</artifactId>
</dependency>
```

## Example
A full example can be found at [`pclib-db-sample`](https://github.com/UnKabaraQuiDev/pclib-db-sample).

## What it contains

This module adds:

* Spring Boot auto-configuration
* `application.yml` based database connector creation
* support for multiple named connectors
* MySQL, SQLite, and PostgreSQL connector configuration
* provider-based DBMS discovery
* Spring-aware type conversion support
* proxy support for abstract table methods annotated with `@Query`
* transaction-aware proxy support
* automatic database, table, and view creation

## Supported protocols

The Spring integration uses the same `DbmsProvider` system as `pclib-db`.

| Protocol | Connector bean type | Notes |
|---|---|---|
| `mysql` | `MySQLDataBaseConnector` | Supports `host`, `port`, `username`, `password`, `characterSet`, `collation`, and `engine`. |
| `sqlite` | `SQLiteDataBaseConnector` | Supports `dirPath` / `dir-path`. The database name is used as the file name. |
| `postgres` / `postgresql` | `PostgreSQLDataBaseConnector` | Supports `host`, `port`, `username`, `password`, and `maintenanceDatabase` / `maintenance-database`. |

## Basic table example

```java
public class PersonData implements DataBaseEntry {

  @Column
  @PrimaryKey
  @AutoIncrement
  protected long id;

  @Column
  @Unique
  protected @MaxLength(35) String name;

  public PersonData() {
  }

  public PersonData(String name) {
    this.name = name;
  }
}
```

```java
@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

  public PersonTable(DataBase dataBase) {
    super(dataBase);
  }

  @Query(columns = { "name" })
  public abstract Optional<PersonData> byName(String name);
}
```

`DeferredDataBaseTable` allows abstract methods to be proxied at runtime. The `@Query` method above is implemented by the Spring interceptor.

If `@Query` has no `value` and no `columns`, the generated query can use method parameter annotations instead:

```java
@Query
public abstract List<PersonData> search(
    @Param(value = "name", comparator = "LIKE", ignoreNull = true) String name,
    @Param(value = "age", comparator = ">=", ignoreNull = true) Integer minAge,
    @Limit int limit,
    @Offset int offset);
```

This creates a `SELECT *` query, adds one condition per `@Param`, skips null values only when `ignoreNull = true`, also support parameters with `@Limit` or `@Offset`.

## Recommended configuration style

Use named connector sections under `pclib.db`.

```yaml
pclib:
  db:
    enabled: true
    expose-connector: true
    expose-database: true
    auto-create: true

    main:
      protocol: mysql
      name: app_db
      host: localhost
      port: 3306
      username: user
      password: pass
      character-set: utf8mb4
      collation: utf8mb4_general_ci
      engine: InnoDB

    reporting:
      protocol: postgres
      name: reporting_db
      host: localhost
      port: 5432
      username: user
      password: pass
      maintenance-database: postgres

    localStore:
      protocol: sqlite
      name: local_store.sqlite
      dir-path: ./data
```

For this example, Spring creates these beans:

| Connector section | Database bean | Connector bean | Entry utils bean |
|---|---|---|---|
| `main` | `main` | `mainConnector` | `mainDataBaseEntryUtils` |
| `reporting` | `reporting` | `reportingConnector` | `reportingDataBaseEntryUtils` |
| `localStore` | `localStore` | `localStoreConnector` | `localStoreDataBaseEntryUtils` |

When more than one connector exists, inject databases by qualifier:

```java
@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

  public PersonTable(@Qualifier("main") DataBase dataBase) {
    super(dataBase);
  }
}
```

## Bean naming rules

Each connector section can define a `qualifier`.

```yaml
pclib:
  db:
    connector1:
      qualifier: mainDb
      protocol: mysql
      name: app_db
      host: localhost
      username: user
      password: pass
```

This creates:

* `mainDb` as `DeferredDataBase`
* `mainDbConnector` as `DataBaseConnectorFactory`
* `mainDbDataBaseEntryUtils` when there is more than one connector

If no `qualifier` is set, the section name is used.

## Global options

These options can be set globally:

```yaml
pclib:
  db:
    enabled: true
    expose-connector: true
    expose-database: true
    auto-create: true
```

| Option | Default | Meaning |
|---|---:|---|
| `enabled` | `true` | Enables or disables PCLib DB auto-configuration. |
| `expose-connector` | `true` | Creates `DataBaseConnectorFactory` beans. |
| `expose-database` | `true` | Creates `DeferredDataBase` beans. |
| `auto-create` | `true` | Creates databases, tables, and views when the Spring context starts. |

Connector sections can override the global flags:

```yaml
pclib:
  db:
    auto-create: true

    readonlyReports:
      protocol: postgres
      name: reports
      host: localhost
      username: user
      password: pass
      expose-connector: false
      auto-create: false
```
## Single connector examples

### MySQL

```yaml
pclib:
  db:
    main:
      protocol: mysql
      name: app_db
      host: localhost
      port: 3306
      username: user
      password: pass
```

### PostgreSQL

```yaml
pclib:
  db:
    main:
      protocol: postgres
      name: app_db
      host: localhost
      port: 5432
      username: user
      password: pass
      maintenance-database: postgres
```
You can also use:

```yaml
protocol: postgresql
```

### SQLite

```yaml
pclib:
  db:
    localStore:
      protocol: sqlite
      name: app.sqlite
      dir-path: ./data
```

## Manual bean configuration

You can still define a database bean yourself:

```java
@Configuration
public class DBConfiguration {

  @Bean
  DeferredDataBase dataBase(DataBaseEntryUtils entryUtils) {
    return new DeferredDataBase(
        () -> new MySQLDataBaseConnector("user", "pass", "localhost", 3306),
        "app_db",
        entryUtils
    );
  }

}
```

Use this if you need custom connector creation that is not covered by `application.yml`.

## Auto creation

`DataBaseInitializer` runs when the Spring context is refreshed. For every exposed database with `auto-create: true`, it calls:

1. `db.create()`
2. `table.create()` for Spring table beans
3. `view.create()` for Spring view beans

Tables and views are sorted by dependency order where possible.

Disable this for a connector when you want to manage the schema yourself:

```yaml
pclib:
  db:
    production:
      protocol: postgres
      name: prod_db
      host: db.example.com
      username: app
      password: secret
      auto-create: false
```

## Spring type support

`SpringDataBaseEntryUtils` extends the base proxy utilities and adds Spring conversion support. It also adds JSON/list support through Jackson and Spring's `ConversionService`.

When exactly one connector is configured, the default `SpringDataBaseEntryUtils` uses that connector's protocol-specific column registry. When multiple connectors are configured, each connector gets its own qualified `DataBaseEntryUtils` bean.

## Custom DBMS providers

Register a custom provider as a Spring bean:

```java
@Bean
DbmsProvider myDbmsProvider() {
  return new MyDbmsProvider();
}
```

The provider can supply:

* a protocol name
* a column type registry
* a SQL structure visitor
* a connector factory for `application.yml` properties

Spring also registers those providers with `DbmsProviders`, so non-Spring code can resolve them too.
