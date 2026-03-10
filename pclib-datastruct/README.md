# PCLib Datastruct

Custom data structures for Java.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-datastruct</artifactId>
</dependency>
````

## What it contains

This module includes:

* pairs and triplets
* tuples
* linked lists
* weak lists and weak sets
* list-backed maps

Main packages:

* `lu.kbra.pclib.datastructure.pair`
* `lu.kbra.pclib.datastructure.triplet`
* `lu.kbra.pclib.datastructure.tuple`
* `lu.kbra.pclib.datastructure.list`
* `lu.kbra.pclib.datastructure.map`
* `lu.kbra.pclib.datastructure.set`

## Example

```java
import lu.kbra.pclib.datastructure.pair.Pair;

Pair<String, Integer> pair = new Pair<>("age", 17);
System.out.println(pair.getKey());
System.out.println(pair.getValue());

ReadOnlyPair<String, Integer> roPair = Pairs.readOnly("age", 17);
```
