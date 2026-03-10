# PCLib Pointer

Mutable wrappers for objects and primitive values.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-pointer</artifactId>
</dependency>
````

## What it contains

This module includes pointer-like wrappers for:

* objects
* strings
* booleans
* bytes
* chars
* doubles
* ints
* longs
* shorts

## Example

```java
import lu.kbra.pclib.pointer.prim.IntPointer;

IntPointer counter = new IntPointer(0);
counter.increment();
System.out.println(counter.get());
```