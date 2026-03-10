# PCLib Concurrency

Small helpers for threads, async chaining, and trigger latches.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-concurrency</artifactId>
</dependency>
````

## What it contains

This module includes:

* `ThreadBuilder`
* async task helpers
* trigger latch implementations for values, objects, lists, and counters

Main classes:

* `ThreadBuilder`
* `CountTriggerLatch`
* `DeferredTriggerLatch`
* `GenericTriggerLatch`
* `ListTriggerLatch`
* `ObjectTriggerLatch`

## Example

```java
import lu.kbra.pclib.ThreadBuilder;

Thread thread = ThreadBuilder.create(() -> System.out.println("running"))
    .setName("worker-thread")
    .start();
```