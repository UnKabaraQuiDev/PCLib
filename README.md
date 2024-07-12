# PCLib

### This is a collection of classes and methods I use in other projects

### Includes:
- Triplet, ReadOnlyTriplet, Triplets<br>
"Pairs" 3 objects together.

- Pair, ReadOnlyPair, Pairs<br>
Pairs 2 objects together.

- JavaPointer, ObjectPointer, PrimitivePointer<br>
A replacement for pointers like in low-level languages, useful for primitive types.

- GlobalLogger, PCLogger<br>
Useful to easily log into files, GlobalLogger contains a static instance of PCLogger.<br>
Config example in: [logs.properties](logs.properties)

- EventManager, Async/SyncEventManager, Event, Listsner, @EventHandler, EventDispatcher, @ListenerPriority<br>
Easily dispatch events to different Listeners

- ExceptionSupplier<T><br>
Supplier that can throw an Exception

- ThreadBuilder<br>
Easily build Thread in a single statement

- PCUtils<br>
Static class containing utility methods

