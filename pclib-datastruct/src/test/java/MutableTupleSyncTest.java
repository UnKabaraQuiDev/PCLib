import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lu.kbra.pclib.datastructure.tuple.MutableOctupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutablePair;
import lu.kbra.pclib.datastructure.tuple.MutablePairImpl;
import lu.kbra.pclib.datastructure.tuple.MutableQuadrupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableQuintupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableSeptupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableSextupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableTripletImpl;
import lu.kbra.pclib.datastructure.tuple.MutableTuple;
import lu.kbra.pclib.datastructure.tuple.Tuple;
import lu.kbra.pclib.datastructure.tuple.TupleView;
import lu.kbra.pclib.datastructure.tuple.Tuples;

/** Behavioral and concurrency regression tests for tuple sizes 2 through 8. */
class MutableTupleSyncTest {

	private static final String[] POSITION_NAMES = { "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth" };

	static Stream<Arguments> sizes() {
		return Stream.of(Arguments.of("Pair", (Supplier<MutableTuple>) () -> new MutablePairImpl<>("a", "b"), 2),
				Arguments.of("Triplet", (Supplier<MutableTuple>) () -> new MutableTripletImpl<>("a", "b", "c"), 3),
				Arguments.of("Quadruple", (Supplier<MutableTuple>) () -> new MutableQuadrupleImpl<>("a", "b", "c", "d"), 4),
				Arguments.of("Quintuple", (Supplier<MutableTuple>) () -> new MutableQuintupleImpl<>("a", "b", "c", "d", "e"), 5),
				Arguments.of("Sextuple", (Supplier<MutableTuple>) () -> new MutableSextupleImpl<>("a", "b", "c", "d", "e", "f"), 6),
				Arguments.of("Septuple", (Supplier<MutableTuple>) () -> new MutableSeptupleImpl<>("a", "b", "c", "d", "e", "f", "g"), 7),
				Arguments
						.of("Octuple", (Supplier<MutableTuple>) () -> new MutableOctupleImpl<>("a", "b", "c", "d", "e", "f", "g", "h"), 8));
	}

	private static Method method(final Class<?> type, final String name, final int parameters) {
		return Arrays.stream(type.getMethods())
				.filter(m -> m.getName().equals(name) && m.getParameterCount() == parameters && !m.isBridge())
				.findFirst()
				.orElseThrow(() -> new AssertionError("Missing " + type.getSimpleName() + "." + name));
	}

	private static Method functionalMethod(final Class<?> type, final String name, final int parameters) {
		return Arrays.stream(type.getMethods())
				.filter(m -> m.getName().equals(name) && m.getParameterCount() == parameters && !m.isBridge())
				.filter(m -> Arrays.stream(m.getParameterTypes()).allMatch(Class::isInterface))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Missing functional " + name));
	}

	private static Object call(final Object target, final String name, final Object... arguments) {
		try {
			return MutableTupleSyncTest.method(target.getClass(), name, arguments.length).invoke(target, arguments);
		} catch (final ReflectiveOperationException e) {
			throw new AssertionError("Failed to invoke " + name, e);
		}
	}

	private static Object function(final Class<?> type, final String result) {
		if (type == Function.class) {
			return (Function<Object, String>) ignored -> result;
		}
		return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> {
			if ("apply".equals(method.getName())) {
				return result;
			}
			throw new UnsupportedOperationException(method.getName());
		});
	}

	private static String part(final String size, final int index) {
		if ("Pair".equals(size)) {
			return index == 0 ? "Key" : "Value";
		}
		return MutableTupleSyncTest.POSITION_NAMES[index];
	}

	@ParameterizedTest(name = "{0}: copy and synchronized identity")
	@MethodSource("sizes")
	void asSynchronizedCreatesCopyButIsIdempotentOnSync(final String name, final Supplier<MutableTuple> factory, final int size) {
		final MutableTuple original = factory.get();
		final MutableTuple sync = original.asSynchronized();
		Assertions.assertNotSame(original, sync);
		Assertions.assertFalse(original.isSynchronized());
		Assertions.assertTrue(sync.isSynchronized());
		Assertions.assertSame(sync, sync.asSynchronized());
		Assertions.assertArrayEquals(original.asArray(), sync.asArray());
		Assertions.assertEquals(size, sync.elementCount());

		original.set(0, "original-only");
		Assertions.assertNotEquals(original.get(0), sync.<Object>get(0));
		sync.set(1, "sync-only");
		Assertions.assertNotEquals(original.get(1), sync.<Object>get(1));
		Assertions.assertTrue(sync.isMutable());
		Assertions.assertFalse(sync.isView());
	}

	@ParameterizedTest(name = "{0}: Tuples synchronized factories")
	@MethodSource("sizes")
	void synchronizedFactoriesSupportValuesAndCopies(final String name, final Supplier<MutableTuple> factory, final int size)
			throws Exception {
		final MutableTuple source = factory.get();
		final MutableTuple copy = source.asSynchronized();
		Assertions.assertNotSame(source, copy);
		Assertions.assertTrue(copy.isSynchronized());
		Assertions.assertArrayEquals(source.asArray(), copy.asArray());

		final Class<?>[] parameterTypes = new Class<?>[size];
		Arrays.fill(parameterTypes, Object.class);
		final Method fromValues = Tuples.class.getMethod("synchronized_", parameterTypes);
		final MutableTuple created = (MutableTuple) fromValues.invoke(null, source.asArray());
		Assertions.assertTrue(created.isSynchronized());
		Assertions.assertArrayEquals(source.asArray(), created.asArray());
	}

	@ParameterizedTest(name = "{0}: named and indexed accessors")
	@MethodSource("sizes")
	void allGettersAndSetters(final String name, final Supplier<MutableTuple> factory, final int size) {
		final MutableTuple tuple = factory.get().asSynchronized();
		for (int i = 0; i < size; i++) {
			final String suffix = MutableTupleSyncTest.part(name, i);
			Assertions.assertEquals(tuple.get(i), MutableTupleSyncTest.call(tuple, "get" + suffix));
			Assertions.assertEquals(true, MutableTupleSyncTest.call(tuple, "has" + suffix));
			MutableTupleSyncTest.call(tuple, "set" + suffix, "named-" + i);
			Assertions.assertEquals("named-" + i, tuple.get(i));
			tuple.set(i, "indexed-" + i);
			Assertions.assertEquals("indexed-" + i, MutableTupleSyncTest.call(tuple, "get" + suffix));
		}
		Assertions.assertArrayEquals(tuple.asArray(), tuple.asArray((IntFunction<String[]>) String[]::new));
		Assertions.assertArrayEquals(tuple.asArray(), tuple.asArray((Function<Integer, String[]>) String[]::new));
		Assertions.assertArrayEquals(tuple.asArray(), tuple.asArray((Supplier<String[]>) () -> new String[size]));
		Assertions.assertArrayEquals(tuple.asArray(), tuple.asArray(new String[size]));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(-1));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(size));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> tuple.set(size, "invalid"));
	}

	@ParameterizedTest(name = "{0}: all named map and mut methods")
	@MethodSource("sizes")
	void allNamedMappingAndMutationFunctions(final String name, final Supplier<MutableTuple> factory, final int size) throws Exception {
		final MutableTuple tuple = factory.get().asSynchronized();
		for (int i = 0; i < size; i++) {
			final String suffix = MutableTupleSyncTest.part(name, i);
			final Method map = MutableTupleSyncTest.method(tuple.getClass(), "map" + suffix, 1);
			final Tuple mapped = (Tuple) map.invoke(tuple, MutableTupleSyncTest.function(map.getParameterTypes()[0], "mapped"));
			Assertions.assertEquals("mapped", mapped.get(i));
			Assertions.assertNotEquals("mapped", tuple.get(i));

			final Method mut = MutableTupleSyncTest.method(tuple.getClass(), "mut" + suffix, 1);
			mut.invoke(tuple, MutableTupleSyncTest.function(mut.getParameterTypes()[0], "mutated"));
			Assertions.assertEquals("mutated", tuple.get(i));
		}

		final Method indexedMap = tuple.getClass().getMethod("map", int.class, Function.class);
		final Tuple mapped = (Tuple) indexedMap.invoke(tuple, 0, (Function<Object, String>) ignored -> "indexed-map");
		Assertions.assertEquals("indexed-map", mapped.get(0));
		tuple.mut(0, ignored -> "indexed-mut");
		Assertions.assertEquals("indexed-mut", tuple.get(0));

		final Method mapAll = MutableTupleSyncTest.functionalMethod(tuple.getClass(), "map", size);
		final Object[] transforms = new Object[size];
		for (int i = 0; i < size; i++) {
			transforms[i] = MutableTupleSyncTest.function(mapAll.getParameterTypes()[i], "map-all-" + i);
		}
		final Tuple allMapped = (Tuple) mapAll.invoke(tuple, transforms);
		for (int i = 0; i < size; i++) {
			Assertions.assertEquals("map-all-" + i, allMapped.get(i));
		}
		final Method scalarMap = MutableTupleSyncTest.functionalMethod(tuple.getClass(), "map", 1);
		Assertions.assertEquals("scalar-result",
				scalarMap.invoke(tuple, MutableTupleSyncTest.function(scalarMap.getParameterTypes()[0], "scalar-result")));

		final Method mutAll = MutableTupleSyncTest.functionalMethod(tuple.getClass(), "mut", size);
		for (int i = 0; i < size; i++) {
			transforms[i] = MutableTupleSyncTest.function(mutAll.getParameterTypes()[i], "mut-all-" + i);
		}
		mutAll.invoke(tuple, transforms);
		for (int i = 0; i < size; i++) {
			Assertions.assertEquals("mut-all-" + i, tuple.get(i));
		}
	}

	@ParameterizedTest(name = "{0}: clones and deep clones")
	@MethodSource("sizes")
	void allCloneVariants(final String name, final Supplier<MutableTuple> factory, final int size) {
		final MutableTuple tuple = factory.get().asSynchronized();
		final Tuple[] copies = {
				tuple.clone(),
				tuple.cloneMutable(),
				tuple.cloneReadOnly(),
				tuple.deepClone(),
				tuple.deepCloneMutable(),
				tuple.deepCloneReadOnly() };
		for (final Tuple copy : copies) {
			Assertions.assertNotSame(tuple, copy);
			Assertions.assertArrayEquals(tuple.asArray(), copy.asArray());
		}
		Assertions.assertFalse(copies[2].isMutable());
		Assertions.assertFalse(copies[5].isMutable());
		tuple.set(0, "new value");
		for (final Tuple copy : copies) {
			Assertions.assertNotEquals("new value", copy.get(0));
		}
	}

	@ParameterizedTest(name = "{0}: views and Object methods")
	@MethodSource("sizes")
	void viewsAndObjectMethods(final String name, final Supplier<MutableTuple> factory, final int size) {
		final MutableTuple tuple = factory.get().asSynchronized();
		final MutableTuple mutableView = tuple.asViewMutable();
		final Tuple readOnlyView = tuple.asViewReadOnly();
		Assertions.assertTrue(mutableView.isView());
		Assertions.assertTrue(readOnlyView.isView());
		Assertions.assertTrue(mutableView.isMutable());
		Assertions.assertFalse(readOnlyView.isMutable());
		Assertions.assertSame(tuple, ((TupleView) mutableView).getParent());
		Assertions.assertSame(tuple, ((TupleView) readOnlyView).getParent());
		tuple.set(0, "after-view");
		Assertions.assertEquals("after-view", mutableView.get(0));
		Assertions.assertEquals("after-view", readOnlyView.get(0));
		Assertions.assertEquals(tuple.hashCode(), mutableView.hashCode());
		Assertions.assertEquals(tuple.toString(), mutableView.toString());
		Assertions.assertEquals(tuple.hashCode(), readOnlyView.hashCode());
	}

	@ParameterizedTest(name = "{0}: synchronized method modifiers")
	@MethodSource("sizes")
	void syncPublicMethodsUseIntrinsicLock(final String name, final Supplier<MutableTuple> factory, final int size) {
		final MutableTuple sync = factory.get().asSynchronized();
		for (final Method method : sync.getClass().getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers()) && !method.isSynthetic() && !method.isBridge()) {
				Assertions.assertTrue(Modifier.isSynchronized(method.getModifiers()), method.toString());
			}
		}
	}

	@Test
	void parallelMutationsAreAtomic() throws Exception {
		final MutablePair<Integer, Integer> counter = Tuples.synchronized_(0, 0);
		final ExecutorService pool = Executors.newFixedThreadPool(4);
		try {
			final Future<?>[] jobs = new Future[4];
			for (int t = 0; t < jobs.length; t++) {
				jobs[t] = pool.submit(() -> {
					for (int i = 0; i < 2_000; i++) {
						counter.mutKey((first, second) -> first + 1);
					}
				});
			}
			for (final Future<?> job : jobs) {
				job.get(30, TimeUnit.SECONDS);
			}
			Assertions.assertEquals(8_000, counter.getKey());
		} finally {
			pool.shutdownNow();
		}
	}

}
