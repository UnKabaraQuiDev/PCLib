# PCLib Function

Extra functional interfaces for Java.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-function</artifactId>
</dependency>
````

## What it contains

This module adds functional interfaces that are often missing from the JDK, such as:

* `TriFunction`
* `TriConsumer`
* `ThrowingFunction`
* `ThrowingConsumer`
* `ThrowingRunnable`
* `ThrowingSupplier`

These are useful when you want lambda-style code with checked exceptions or more than two parameters.

## Example

```java
import lu.kbra.pclib.impl.TriFunction;

TriFunction<Integer, Integer, Integer, Integer> add3 = (a, b, c) -> a + b + c;
int result = add3.apply(1, 2, 3);
System.out.println(result);

ThrowingFunction<Boolean, String, IllegalArgumentException> boolToString = (b) -> {
  if (b == null) {
    throw new IllegalArgumentException("Input was null.");
  } else if (b) {
    return "true";
  } else {
    return "false";
  }
};
```