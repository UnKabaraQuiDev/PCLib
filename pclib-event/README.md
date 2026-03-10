 PCLib Event

A lightweight event system with synchronous and asynchronous dispatch.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-event</artifactId>
</dependency>
````

## What it contains

This module provides:

* event managers
* event listeners
* event handler annotations
* listener priorities
* sync and async dispatch

Main classes:

* `EventManager`
* `SyncEventManager`
* `AsyncEventManager`
* `EventListener`
* `EventHandler`

## Example

```java
import lu.kbra.pclib.listener.*;

public class MyEvent implements Event {

}

public class MyListener implements EventListener {

    @EventHandler
    public void onEvent(MyEvent event) {
        System.out.println("received");
    }

}

SyncEventManager eventManager = new SyncEventManager();
eventManager.register(new MyListener());
eventManager.dispatch(new MyEvent());
// same for AsyncEventManager
```