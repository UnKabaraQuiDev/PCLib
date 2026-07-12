# PCLib
#### v1.1.0 / v1.2.0-SNAPSHOT

[![Maven Package (Nightly)](https://github.com/UnKabaraQuiDev/PCLib/actions/workflows/nightly.yml/badge.svg)](https://github.com/UnKabaraQuiDev/PCLib/actions/workflows/nightly.yml)

PCLib is a multi-module Java library with small, reusable utilities for other projects.

**Java version:** Java 8 except `pclib-db-spring` requires Java 17

## ToDo [here](https://github.com/users/UnKabaraQuiDev/projects/6)

## Contact [email](mailto:u.kbra.lu+pclib@gmail.com)

## Modules

- `pclib-common` - shared helpers and core utilities
- `pclib-datastruct` - custom data structures like pairs, triplets, tuples, lists, maps, and weak collections
- `pclib-function` - functional interfaces such as throwing functions and tri-functions
- `pclib-awt` - AWT helpers for drawing and image processing
- `pclib-swing` - Swing components like charts and label builders
- `pclib-db` - database helpers, SQL builders, annotations, and query utilities
- `pclib-db-spring` - Spring integration for `pclib-db`
- `pclib-cache` - cache utilities (incomplete)
- `pclib-concurrency` - thread and latch utilities
- `pclib-json` - JSON config loading helpers (deprecated)
- `pclib-event` - synchronous and asynchronous event system
- `pclib-logger` - lightweight logging utilities
- `pclib-pointer` - mutable wrappers for objects and primitive types
- `pclib-jbcodec` - byte encoding and decoding library
- `pclib-packets4j` - lightweight TCP packet library
- `pclib-parser` - Small code parser module

## Maven repository

```xml
<repositories>
  <repository>
    <!-- both snapshots and releases -->
    <id>nexus.kbra.lu-public</id>
    <url>https://nexus.kbra.lu/repository/maven-public/</url>
  </repository>
  <repository>
    <!-- only releases -->
    <id>nexus.kbra.lu-releases</id>
    <url>https://nexus.kbra.lu/repository/maven-releases/</url>
  </repository>
  <repository>
    <!-- only snapshots -->
    <id>nexus.kbra.lu-snapshots</id>
    <url>https://nexus.kbra.lu/repository/maven-snapshots/</url>
  </repository>
</repositories>
```

## Use the parent BOM / dependency management

If you import the parent POM in your `dependencyManagement`, you can omit versions for all PCLib modules.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>lu.kbra</groupId>
      <artifactId>pclib</artifactId>
      <version>1.1.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

## Add a submodule dependency

Example with `pclib-common`:

```xml
<dependencies>
  <dependency>
    <groupId>lu.kbra</groupId>
    <artifactId>pclib-common</artifactId>
  </dependency>
</dependencies>
```

## Build

Build the full project with Maven, add `-DskipTests` to skip the test. The DB tests require docker or a local MySQL server running.

```bash
git clone git@github.com:UnKabaraQuiDev/PCLib.git pclib
cd pclib
mvn clean install
```
