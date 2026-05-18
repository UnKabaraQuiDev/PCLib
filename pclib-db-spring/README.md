# PCLib DB Spring

Spring integration for `pclib-db`.

**Java version:** Java 17  
This module uses a newer Java version than the main project.

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-db-spring</artifactId>
</dependency>
````

## What it contains

This module adds Spring support on top of `pclib-db`, including:

* Spring auto-configuration
* factory beans for queryable objects
* method interceptors for `@Query`
* deferred database helpers
* Spring-aware database entry utilities

Main packages:

* `lu.kbra.pclib.db.config`
* `lu.kbra.pclib.db.factory`
* `lu.kbra.pclib.db.intercept`
* `lu.kbra.pclib.db.registrar`

## Example

### Data classes

```java
public class PersonData implements DataBaseEntry {

  @Column
  @PrimaryKey
  @AutoIncrement
  protected long id;

  @Column(length = 35)
  @Unique
  protected String name;

  public PersonData() {
  }

  public PersonData(long id) {
    this.id = id;
  }

  public PersonData(String name) {
    this.name = name;
  }

  public PersonData(long id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    return "PersonData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + "]";
  }

}
```

## Table classes

```java
@Component
// DeferredDataBaseTable allows for abstract methods to be proxied at runtime using @Query
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

  public PersonTable(@Qualifier("dataBase2") DataBase dataBase) {
    super(dataBase);
  }

  @Query(columns = { "name" })
  public abstract Optional<PersonData> byName(String name);

}
```

## Beans
```java
@Configuration
public class DBConfiguration {

  // default SpringDataBaseEntryUtils provided by the PCLib AutoConfiguration
  // DeferredDataBase allows proxying of DeferredDataBaseTables
  @Bean
  public DeferredDataBase dataBase(final DataBaseEntryUtils entryUtils) {
    return new DeferredDataBase(() -> new MySQLDataBaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort()),
        "pclib-db-spring", entryUtils);
  }

}
```

## Provider-based Spring configuration

`pclib-db-spring` can now create connector and database beans from any `DbmsProvider` found through either:

* `ServiceLoader` entries in `META-INF/services/lu.kbra.pclib.db.dbms.DbmsProvider`
* Spring beans of type `DbmsProvider`

A provider can also implement connector creation by overriding:

```java
DataBaseConnectorFactory createConnectorFactory(Map<String, Object> properties)
```

### Multiple connectors

```yaml
pclib:
  db:
    enabled: true
    connector1:
      qualifier: abc
      protocol: mysql
      name: app_db
      host: localhost
      port: 3306
      username: user
      password: pass

    localStore:
      protocol: sqlite
      name: local.sqlite
      dir-path: ./data
```

Bean names are based on `qualifier`. If no qualifier is set, the section name is used.

For the example above, Spring creates:

* `abc` as `DeferredDataBase`
* `abcConnector` as `DataBaseConnectorFactory`
* `localStore` as `DeferredDataBase`
* `localStoreConnector` as `DataBaseConnectorFactory`

With more than one connector, Spring also creates one protocol-specific `DataBaseEntryUtils` bean per connector:

* `abcDataBaseEntryUtils`
* `localStoreDataBaseEntryUtils`

Use `@Qualifier("abc")` or `@Qualifier("localStore")` when injecting a `DataBase` if more than one connector exists.

### Per-connector options

Every connector can override the global flags:

```yaml
pclib:
  db:
    expose-connector: true
    expose-database: true
    auto-create: true

    reporting:
      protocol: mysql
      name: reporting
      expose-connector: false
      auto-create: false
      username: user
      password: pass
```

The old single-connector style is still read as a migration path, but new code should use named connector sections.
