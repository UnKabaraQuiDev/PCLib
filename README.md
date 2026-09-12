# PCLib
#### v1.2.1 / v1.2.2-SNAPSHOT

[![Maven Central](https://img.shields.io/maven-central/v/lu.kbra/pclib.svg)](https://central.sonatype.com/artifact/lu.kbra/pclib)
[![nexus.kbra.lu-public](https://img.shields.io/nexus/s/lu.kbra/pclib?server=https%3A%2F%2Fnexus.kbra.lu&label=nexus.kbra.lu-public)](https://nexus.kbra.lu/service/rest/repository/browse/maven-public/lu/kbra/pclib/)


<p>
  <strong>ToDo</strong> <a href="https://github.com/users/UnKabaraQuiDev/projects/6">here</a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <strong>Issues</strong> <a href="https://github.com/UnKabaraQuiDev/pclib/issues">here</a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <strong>Pull Requests</strong> <a href="https://github.com/UnKabaraQuiDev/pclib/pulls">here</a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <strong>Contact</strong> <a href="mailto:u.kbra.lu+pclib@gmail.com">email</a>
</p>

---

PCLib is a multi-module Java library with small, reusable utilities for other projects.

**Java version:** Java 8 except `pclib-db-spring` requires Java 17

---

## Modules

- `pclib-common` - shared helpers and core utilities
- `pclib-datastruct` - custom data structures like pairs, triplets, tuples, lists, maps, and weak collections
- `pclib-function` - functional interfaces such as throwing functions and tri-functions
- `pclib-awt` - AWT helpers for drawing and image processing
- `pclib-swing` - Swing components like charts and label builders
- `pclib-db-core` - database framework core
- `pclib-db-mysql` - includes mysql connectors for `pblib-db`
- `pclib-db-postgres` - includes postgresql connectors for `pblib-db`
- `pclib-db-sqlite` - includes sqlite connectors for `pblib-db`
- `pclib-db-all` - includes all connectors for `pblib-db` as well as tests
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

<table>
  <thead>
    <tr>
      <th></th>
      <th>nexus.kbra.lu-snapshots</th>
      <th>nexus.kbra.lu-releases</th>
      <th>nexus.kbra.lu-public</th>
      <th>central</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th colspan="5"><em>RELEASES</em></th>
    </tr>
    <tr>
      <td><i>jar</i></td>
      <td>❌</td>
      <td>✅</td>
      <td>✅</td>
      <td>✅</td>
    </tr>
    <tr>
      <td>javadoc</td>
      <td>❌</td>
      <td>✅</td>
      <td>✅</td>
      <td>✅</td>
    </tr>
    <tr>
      <td>sources</td>
      <td>❌</td>
      <td>✅</td>
      <td>✅</td>
      <td>✅</td>
    </tr>
    <tr>
      <td>with-dependencies</td>
      <td>❌</td>
      <td>✅</td>
      <td>✅</td>
      <td>❌</td>
    </tr>
    <tr>
      <td>tests</td>
      <td>❌</td>
      <td>✅</td>
      <td>✅</td>
      <td>✅</td>
    </tr>
    <tr>
      <th colspan="5"><em>SNAPSHOTS</em></th>
    </tr>
    <tr>
      <td><i>jar</i></td>
      <td>✅</td>
      <td>❌</td>
      <td>✅</td>
      <td>❌</td>
    </tr>
    <tr>
      <td>javadoc</td>
      <td>✅</td>
      <td>❌</td>
      <td>✅</td>
      <td>❌</td>
    </tr>
    <tr>
      <td>sources</td>
      <td>✅</td>
      <td>❌</td>
      <td>✅</td>
      <td>❌</td>
    </tr>
    <tr>
      <td>with-dependencies</td>
      <td>✅</td>
      <td>❌</td>
      <td>✅</td>
      <td>❌</td>
    </tr>
    <tr>
      <td>tests</td>
      <td>✅</td>
      <td>❌</td>
      <td>✅</td>
      <td>❌</td>
    </tr>
  </tbody>
</table>


_Prefer using the central repository, only use the others if you need snapshots or `with-dependencies`_
```xml
<repositories>
  <repository>
    <id>nexus.kbra.lu-public</id>
    <url>https://nexus.kbra.lu/repository/maven-public/</url>
  </repository>
  <repository>
    <id>nexus.kbra.lu-releases</id>
    <url>https://nexus.kbra.lu/repository/maven-releases/</url>
  </repository>
  <repository>
    <url>https://nexus.kbra.lu/repository/maven-snapshots/</url>
  </repository>
</repositories>
```

## Use the parent POM / dependency management

If you import the parent POM in your `dependencyManagement`, you can omit versions for all PCLib modules.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>lu.kbra</groupId>
      <artifactId>pclib</artifactId>
      <version>1.2.0</version>
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

Or if using a repository that has other artifacts, you can use the classifier to specify which one you need ("", javadoc, sources, tests, with-dependencies):
```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-common</artifactId>
  <classifier>with-dependencies</classifier>
</dependency>
```

## JavaDoc
You can find the Javadoc for the releases [here](https://pclib.kbra.lu/javadoc/release/)

## Build

Build the full project with Maven, add `-DskipTests` to skip the test. Some tests require a running docker instance.

```bash
git clone git@github.com:UnKabaraQuiDev/PCLib.git pclib
cd pclib
mvn clean install
```

## Signing
All artifacts after v1.2.1 are signed. [Download GPG public key](./pclib-pubkey.asc)

**Fingerprint:** `2D82 735A 84BE 1A75 30CC  3CA1 FB69 741B 6CDA BE2E`
