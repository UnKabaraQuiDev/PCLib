# PCLib

### This is a collection of classes and methods I use in other projects
##### This was made for Java (1.)8

### Includes:
- Triplet, ReadOnlyTriplet, Triplets<br>
"Pairs" 3 objects together.

- Pair, ReadOnlyPair, Pairs<br>
Pairs 2 objects together.

- JavaPointer, ObjectPointer, PrimitivePointer<br>
A replacement for pointers like in low-level languages, useful for primitive types.

- GlobalLogger, PCLogger<br>
Useful to easily log into files, GlobalLogger contains a static instance of PCLogger.<br>
Config example in: [logs.properties](https://github.com/UnKabaraQuiDev/PCLib/blob/main/src/main/java/lu/pcy113/pclib/logger/logs.properties)

- EventManager, Async/SyncEventManager, Event, Listener, @EventHandler, EventDispatcher, @ListenerPriority<br>
Easily dispatch events to different Listeners (uses annotation reflection)

- ThrowingSupplier<T>, ThrowingFunction<T, R>, ThrowingConsumer<T><br>
Supplier that can throw an Exception

- ThreadBuilder<br>
Easily build Thread in a single statement

- PCUtils<br>
Static class containing utility methods

- @DependsOn<br>

- lu.pcy113.pclib.db.*<br>
Minimalistic SQL DataBase framework (uses annotation reflection) (see src/test/java/DBMain)

- ConfigLoader<br>
Loads a config from a properties or json file (uses annotation reflection)

- NextTask<br>
A CompletableFuture replacement (chains multiple operations in one statement) (see src/test/java/NextTaskMain)

- ByteBuddyAgent<br>
A Java agent that loads @MixinClass and @MixinMethod to modify bytecode at runtime (see MixinLoader & PairMixin)
