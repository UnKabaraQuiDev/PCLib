# JVM system properties

PCLib reads the JVM system properties below. Pass them with `-D` when starting Java:

```bash
java -DQueryBuilder.default_entry_limit=500 -DDBException.include_sql_in_exception=false -jar app.jar
```

The **default** applies when the property is not set. Most settings initialize a static field when its class is first loaded, so changing a system property later does not update that field.

## `pclib-db-core`

| JVM property | Type | Default | Effect | Defined in |
| --- | --- | --- | --- | --- |
| `QueryBuilder.default_entry_limit` | `int` | `250` | Sets the initial entry limit for a new `QueryBuilder`. | [QueryBuilder](pclib-db-core/src/main/java/lu/kbra/pclib/db/query/QueryBuilder.java) |
| `DBException.include_structure_in_exception` | `boolean` | `true` | Includes the database structure tree in exception messages. | [DBException](pclib-db-core/src/main/java/lu/kbra/pclib/db/exception/DBException.java) |
| `DBException.include_sql_in_exception` | `boolean` | `true` | Includes the SQL source in exception messages. | [DBException](pclib-db-core/src/main/java/lu/kbra/pclib/db/exception/DBException.java) |
| `DBException.include_query_in_exception` | `boolean` | `true` | Includes the query object in exception messages. | [DBException](pclib-db-core/src/main/java/lu/kbra/pclib/db/exception/DBException.java) |
| `ColumnTypeProvider.include_type_hints_in_exception` | `boolean` | `true` | Includes type hints when column or encoding type resolution fails. The key uses `ColumnTypeProvider`, even though the flag is declared in `DBException`. | [DBException](pclib-db-core/src/main/java/lu/kbra/pclib/db/exception/DBException.java) |
| `DatabaseEntryUtils.force_default_value_on_non_null` | `boolean` | `false` | Rejects a non-nullable, non-auto-increment column without a default value during schema scanning. | [DatabaseEntryUtilsOptionsOwner](pclib-db-core/src/main/java/lu/kbra/pclib/db/utils/impl/DatabaseEntryUtilsOptionsOwner.java) |
| `DatabaseEntryUtils.fail_on_duplicate_factory_method` | `boolean` | `true` | Throws an exception when multiple entry factory methods register the same parameter signature. | [DatabaseEntryUtilsOptionsOwner](pclib-db-core/src/main/java/lu/kbra/pclib/db/utils/impl/DatabaseEntryUtilsOptionsOwner.java) |
| `DatabaseEntryUtils.lazy_constructor_scan` | `boolean` | `false` | Skips eager caching of entry constructors and factories during database scanning. | [DatabaseEntryUtilsOptionsOwner](pclib-db-core/src/main/java/lu/kbra/pclib/db/utils/impl/DatabaseEntryUtilsOptionsOwner.java) |
| `ColumnTypeRegistry.debug_type_names` | `boolean` | `false` | Gives registered column-type selection functions descriptive names for debugging. | [ColumnTypeRegistry](pclib-db-core/src/main/java/lu/kbra/pclib/db/utils/registry/ColumnTypeRegistry.java) |
| `EncodingTypeRegistry.debug_type_names` | `boolean` | `false` | Gives registered encoding-type selection functions descriptive names for debugging. | [EncodingTypeRegistry](pclib-db-core/src/main/java/lu/kbra/pclib/db/utils/registry/EncodingTypeRegistry.java) |

The three `DatabaseEntryUtils.*` values also act as defaults for per-instance options. You can override them on a `DatabaseEntryUtilsOptionsOwner` with `setOption(propertyKey, value)`.

## `pclib-db-sqlite`

| JVM property | Type | Default | Effect | Defined in |
| --- | --- | --- | --- | --- |
| `SQLiteDatabaseConnector.fix_db_extension` | `boolean` | `true` | Appends `.db` to a database filename unless it already ends with `.db` or `.sqlite`. | [SQLiteDatabaseConnector](pclib-db-sqlite/src/main/java/lu/kbra/pclib/db/connector/SQLiteDatabaseConnector.java) |
| `SQLiteStructureVisitor.clear_instead_of_truncate` | `boolean` | `false` | Makes a requested table truncation generate `DELETE FROM ...` instead. When false, truncation throws because SQLite does not support `TRUNCATE`. | [SQLiteStructureVisitor](pclib-db-sqlite/src/main/java/lu/kbra/pclib/db/dbms/SQLiteStructureVisitor.java) |

`SQLiteStructureVisitor.clear_instead_of_truncate` can also be overridden through the visitor's option map.

## `pclib-packets4j`

| JVM property | Type | Default | Effect | Defined in |
| --- | --- | --- | --- | --- |
| `P4J_maxPacketSize` | `int` | `2048` | Sets the maximum packet size for both clients and servers. Each `P4JClient` or `P4JServer` constructor reads it into the corresponding static `MAX_PACKET_SIZE` field. | [P4JClient](pclib-packets4j/src/main/java/lu/kbra/p4j/socket/client/P4JClient.java), [P4JServer](pclib-packets4j/src/main/java/lu/kbra/p4j/socket/server/P4JServer.java) |

An absent or invalid integer leaves the current `MAX_PACKET_SIZE` unchanged. The initial value is `2048` for both classes.

## `pclib-db-spring`

| JVM property | Type | Default | Effect | Read in |
| --- | --- | --- | --- | --- |
| `spring.aot.processing` | `boolean` | `false` | Tells the queryable registrars that Spring is processing ahead-of-time (AOT) configuration. PCLib reads Spring's `AbstractAotProcessor.AOT_PROCESSING` constant; Spring manages this flag during AOT builds. | [SQLQueryableRegistrar](pclib-db-spring/src/main/java/lu/kbra/pclib/db/registrar/SQLQueryableRegistrar.java), [QueryableTemplateRegistrar](pclib-db-spring/src/main/java/lu/kbra/pclib/db/registrar/QueryableTemplateRegistrar.java) |

Use Spring's `process-aot` build step for native builds. Setting `spring.aot.processing` by itself does not generate AOT code.

## `pclib-db-all` (test code only)

These properties belong to test helpers, not the database modules' production configuration.

| JVM property | Type | Default | Effect | Defined in |
| --- | --- | --- | --- | --- |
| `MySQL.use_local_db_if_available` | `boolean` | `true` | Uses a reachable local MySQL server with the test credentials when one is available; otherwise starts a Testcontainers database. | [MySQL test helper](pclib-db-all/src/test/java/mysql/MySQL.java) |
| `PostgreSQL.use_local_db_if_available` | `boolean` | `true` | Uses a reachable local PostgreSQL server with the test credentials when one is available; otherwise starts a Testcontainers database. | [PostgreSQL test helper](pclib-db-all/src/test/java/postgres/PostgreSQL.java) |
