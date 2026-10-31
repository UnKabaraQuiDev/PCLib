# PCLib
#### v1.2.4 / v1.2.5-SNAPSHOT

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

**Java version:** Java 8 for most modules; `pclib-db-spring` requires Java 17.

---

## Modules

| Module | Purpose | Java | GraalVM Support |
| --- | --- | :---: | :---: |
| [pclib-common](./pclib-common/) | Shared helpers and core utilities | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-datastruct](./pclib-datastruct/) | Tuples, lists, maps, and other data structures | 8 | ![Verified](https://img.shields.io/badge/%E2%97%8F-supported-2ea043.svg) |
| [pclib-function](./pclib-function/) | Functional interfaces | 8 | ![Verified](https://img.shields.io/badge/%E2%97%8F-supported-2ea043.svg) |
| [pclib-awt](./pclib-awt/) | AWT drawing and image helpers | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-swing](./pclib-swing/) | Swing charts and components | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-db-core](./pclib-db-core/) | Database mapping, queries, and migrations | 8 | ![Unsupported alone](https://img.shields.io/badge/%E2%97%8F-unsupported-d73a49.svg) |
| [pclib-db-mysql](./pclib-db-mysql/) | MySQL provider and JDBC connector | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-with--spring-e3a008.svg) |
| [pclib-db-sqlite](./pclib-db-sqlite/) | SQLite provider and JDBC connector | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-with--spring-e3a008.svg) |
| [pclib-db-postgres](./pclib-db-postgres/) | PostgreSQL provider and JDBC connector | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-with--spring-e3a008.svg) |
| [pclib-db-all](./pclib-db-all/) | All three database providers and integration tests | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-with--spring-e3a008.svg) |
| [pclib-db-spring](./pclib-db-spring/) | Spring Boot integration and native-image AOT support | 17 | ![Supported](https://img.shields.io/badge/%E2%97%8F-supported-2ea043.svg) |
| [pclib-cache](./pclib-cache/) | Cache utilities (incomplete) | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-concurrency](./pclib-concurrency/) | Thread and latch utilities | 8 | ![Verified](https://img.shields.io/badge/%E2%97%8F-supported-2ea043.svg) |
| [pclib-event](./pclib-event/) | Synchronous and asynchronous events | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-logger](./pclib-logger/) | Logging utilities | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-pointer](./pclib-pointer/) | Mutable references for objects and primitives | 8 | ![Verified](https://img.shields.io/badge/%E2%97%8F-supported-2ea043.svg) |
| [pclib-jbcodec](./pclib-jbcodec/) | Byte encoding and decoding | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-packets4j](./pclib-packets4j/) | TCP packets | 8 | ![Unverified](https://img.shields.io/badge/%E2%97%8F-unverified-e3a008.svg) |
| [pclib-parser](./pclib-parser/) | Code and token parsing | 8 | ![Verified](https://img.shields.io/badge/%E2%97%8F-supported-2ea043.svg) |

**GraalVM status:** Green means supported or tested, orange means it may work but has not been fully verified and some features may not fully work, and red means the listed setup is not supported.

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
      <version>1.2.4</version>
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
