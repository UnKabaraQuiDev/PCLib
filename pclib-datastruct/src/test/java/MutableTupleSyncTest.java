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

import lu.kbra.pclib.datastructure.tuple.MutableOctuple;
import lu.kbra.pclib.datastructure.tuple.MutableOctupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutablePair;
import lu.kbra.pclib.datastructure.tuple.MutablePairImpl;
import lu.kbra.pclib.datastructure.tuple.MutableQuadruple;
import lu.kbra.pclib.datastructure.tuple.MutableQuadrupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableQuintuple;
import lu.kbra.pclib.datastructure.tuple.MutableQuintupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableSeptuple;
import lu.kbra.pclib.datastructure.tuple.MutableSeptupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableSextuple;
import lu.kbra.pclib.datastructure.tuple.MutableSextupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutableTriplet;
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

		final MutableTuple created;

		switch (size) {
		case 2:
			created = Tuples.synchronized_("a", "b");
			break;
		case 3:
			created = Tuples.synchronized_("a", "b", "c");
			break;
		case 4:
			created = Tuples.synchronized_("a", "b", "c", "d");
			break;
		case 5:
			created = Tuples.synchronized_("a", "b", "c", "d", "e");
			break;
		case 6:
			created = Tuples.synchronized_("a", "b", "c", "d", "e", "f");
			break;
		case 7:
			created = Tuples.synchronized_("a", "b", "c", "d", "e", "f", "g");
			break;
		case 8:
			created = Tuples.synchronized_("a", "b", "c", "d", "e", "f", "g", "h");
			break;
		default:
			throw new AssertionError(size);
		}
		Assertions.assertTrue(created.isSynchronized());
		Assertions.assertArrayEquals(source.asArray(), created.asArray());
	}

	@ParameterizedTest(name = "{0}: named and indexed accessors")
	@MethodSource("sizes")
	void allGettersAndSetters(final String name, final Supplier<MutableTuple> factory, final int size) {
		final MutableTuple tuple = factory.get().asSynchronized();
		for (int i = 0; i < size; i++) {
			Assertions.assertEquals(tuple.get(i), MutableTupleSyncTest.namedGet(tuple, size, i));
			Assertions.assertTrue(MutableTupleSyncTest.namedHas(tuple, size, i));
			MutableTupleSyncTest.namedSet(tuple, size, i, "named-" + i);
			Assertions.assertEquals("named-" + i, tuple.get(i));
			tuple.set(i, "indexed-" + i);
			Assertions.assertEquals("indexed-" + i, MutableTupleSyncTest.namedGet(tuple, size, i));
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
			final Tuple mapped = MutableTupleSyncTest.namedMap(tuple, size, i, "mapped");
			Assertions.assertEquals("mapped", mapped.get(i));
			Assertions.assertNotEquals("mapped", tuple.get(i));
			MutableTupleSyncTest.namedMut(tuple, size, i, "mutated");
			Assertions.assertEquals("mutated", tuple.get(i));
		}
		final Tuple mapped = MutableTupleSyncTest.indexedMap(tuple, size, 0, "indexed-map");
		Assertions.assertEquals("indexed-map", mapped.get(0));
		tuple.mut(0, ignored -> "indexed-mut");
		Assertions.assertEquals("indexed-mut", tuple.get(0));
		final Tuple allMapped = MutableTupleSyncTest.mapAll(tuple, size);
		for (int i = 0; i < size; i++) {
			Assertions.assertEquals("map-all-" + i, allMapped.get(i));
		}
		Assertions.assertEquals("scalar-result", MutableTupleSyncTest.scalarMap(tuple, size));
		MutableTupleSyncTest.mutAll(tuple, size);
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

	private static Object namedGet(final MutableTuple tuple, final int size, final int index) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			switch (index) {
			case 0:
				return t.getKey();
			case 1:
				return t.getValue();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.getFirst();
			case 1:
				return t.getSecond();
			case 2:
				return t.getThird();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.getFirst();
			case 1:
				return t.getSecond();
			case 2:
				return t.getThird();
			case 3:
				return t.getFourth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.getFirst();
			case 1:
				return t.getSecond();
			case 2:
				return t.getThird();
			case 3:
				return t.getFourth();
			case 4:
				return t.getFifth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.getFirst();
			case 1:
				return t.getSecond();
			case 2:
				return t.getThird();
			case 3:
				return t.getFourth();
			case 4:
				return t.getFifth();
			case 5:
				return t.getSixth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.getFirst();
			case 1:
				return t.getSecond();
			case 2:
				return t.getThird();
			case 3:
				return t.getFourth();
			case 4:
				return t.getFifth();
			case 5:
				return t.getSixth();
			case 6:
				return t.getSeventh();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.getFirst();
			case 1:
				return t.getSecond();
			case 2:
				return t.getThird();
			case 3:
				return t.getFourth();
			case 4:
				return t.getFifth();
			case 5:
				return t.getSixth();
			case 6:
				return t.getSeventh();
			case 7:
				return t.getEighth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static boolean namedHas(final MutableTuple tuple, final int size, final int index) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasKey();
			case 1:
				return t.hasValue();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasFirst();
			case 1:
				return t.hasSecond();
			case 2:
				return t.hasThird();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasFirst();
			case 1:
				return t.hasSecond();
			case 2:
				return t.hasThird();
			case 3:
				return t.hasFourth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasFirst();
			case 1:
				return t.hasSecond();
			case 2:
				return t.hasThird();
			case 3:
				return t.hasFourth();
			case 4:
				return t.hasFifth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasFirst();
			case 1:
				return t.hasSecond();
			case 2:
				return t.hasThird();
			case 3:
				return t.hasFourth();
			case 4:
				return t.hasFifth();
			case 5:
				return t.hasSixth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasFirst();
			case 1:
				return t.hasSecond();
			case 2:
				return t.hasThird();
			case 3:
				return t.hasFourth();
			case 4:
				return t.hasFifth();
			case 5:
				return t.hasSixth();
			case 6:
				return t.hasSeventh();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.hasFirst();
			case 1:
				return t.hasSecond();
			case 2:
				return t.hasThird();
			case 3:
				return t.hasFourth();
			case 4:
				return t.hasFifth();
			case 5:
				return t.hasSixth();
			case 6:
				return t.hasSeventh();
			case 7:
				return t.hasEighth();
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static void namedSet(final MutableTuple tuple, final int size, final int index, final String value) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			switch (index) {
			case 0:
				t.setKey(value);
				return;
			case 1:
				t.setValue(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			switch (index) {
			case 0:
				t.setFirst(value);
				return;
			case 1:
				t.setSecond(value);
				return;
			case 2:
				t.setThird(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.setFirst(value);
				return;
			case 1:
				t.setSecond(value);
				return;
			case 2:
				t.setThird(value);
				return;
			case 3:
				t.setFourth(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.setFirst(value);
				return;
			case 1:
				t.setSecond(value);
				return;
			case 2:
				t.setThird(value);
				return;
			case 3:
				t.setFourth(value);
				return;
			case 4:
				t.setFifth(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.setFirst(value);
				return;
			case 1:
				t.setSecond(value);
				return;
			case 2:
				t.setThird(value);
				return;
			case 3:
				t.setFourth(value);
				return;
			case 4:
				t.setFifth(value);
				return;
			case 5:
				t.setSixth(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.setFirst(value);
				return;
			case 1:
				t.setSecond(value);
				return;
			case 2:
				t.setThird(value);
				return;
			case 3:
				t.setFourth(value);
				return;
			case 4:
				t.setFifth(value);
				return;
			case 5:
				t.setSixth(value);
				return;
			case 6:
				t.setSeventh(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.setFirst(value);
				return;
			case 1:
				t.setSecond(value);
				return;
			case 2:
				t.setThird(value);
				return;
			case 3:
				t.setFourth(value);
				return;
			case 4:
				t.setFifth(value);
				return;
			case 5:
				t.setSixth(value);
				return;
			case 6:
				t.setSeventh(value);
				return;
			case 7:
				t.setEighth(value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static Tuple namedMap(final MutableTuple tuple, final int size, final int index, final String value) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapKey((a0, a1) -> value);
			case 1:
				return t.mapValue((a0, a1) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapFirst((a0, a1, a2) -> value);
			case 1:
				return t.mapSecond((a0, a1, a2) -> value);
			case 2:
				return t.mapThird((a0, a1, a2) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapFirst((a0, a1, a2, a3) -> value);
			case 1:
				return t.mapSecond((a0, a1, a2, a3) -> value);
			case 2:
				return t.mapThird((a0, a1, a2, a3) -> value);
			case 3:
				return t.mapFourth((a0, a1, a2, a3) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapFirst((a0, a1, a2, a3, a4) -> value);
			case 1:
				return t.mapSecond((a0, a1, a2, a3, a4) -> value);
			case 2:
				return t.mapThird((a0, a1, a2, a3, a4) -> value);
			case 3:
				return t.mapFourth((a0, a1, a2, a3, a4) -> value);
			case 4:
				return t.mapFifth((a0, a1, a2, a3, a4) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapFirst((a0, a1, a2, a3, a4, a5) -> value);
			case 1:
				return t.mapSecond((a0, a1, a2, a3, a4, a5) -> value);
			case 2:
				return t.mapThird((a0, a1, a2, a3, a4, a5) -> value);
			case 3:
				return t.mapFourth((a0, a1, a2, a3, a4, a5) -> value);
			case 4:
				return t.mapFifth((a0, a1, a2, a3, a4, a5) -> value);
			case 5:
				return t.mapSixth((a0, a1, a2, a3, a4, a5) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapFirst((a0, a1, a2, a3, a4, a5, a6) -> value);
			case 1:
				return t.mapSecond((a0, a1, a2, a3, a4, a5, a6) -> value);
			case 2:
				return t.mapThird((a0, a1, a2, a3, a4, a5, a6) -> value);
			case 3:
				return t.mapFourth((a0, a1, a2, a3, a4, a5, a6) -> value);
			case 4:
				return t.mapFifth((a0, a1, a2, a3, a4, a5, a6) -> value);
			case 5:
				return t.mapSixth((a0, a1, a2, a3, a4, a5, a6) -> value);
			case 6:
				return t.mapSeventh((a0, a1, a2, a3, a4, a5, a6) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				return t.mapFirst((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 1:
				return t.mapSecond((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 2:
				return t.mapThird((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 3:
				return t.mapFourth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 4:
				return t.mapFifth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 5:
				return t.mapSixth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 6:
				return t.mapSeventh((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			case 7:
				return t.mapEighth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static void namedMut(final MutableTuple tuple, final int size, final int index, final String value) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			switch (index) {
			case 0:
				t.mutKey((a0, a1) -> value);
				return;
			case 1:
				t.mutValue((a0, a1) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			switch (index) {
			case 0:
				t.mutFirst((a0, a1, a2) -> value);
				return;
			case 1:
				t.mutSecond((a0, a1, a2) -> value);
				return;
			case 2:
				t.mutThird((a0, a1, a2) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.mutFirst((a0, a1, a2, a3) -> value);
				return;
			case 1:
				t.mutSecond((a0, a1, a2, a3) -> value);
				return;
			case 2:
				t.mutThird((a0, a1, a2, a3) -> value);
				return;
			case 3:
				t.mutFourth((a0, a1, a2, a3) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.mutFirst((a0, a1, a2, a3, a4) -> value);
				return;
			case 1:
				t.mutSecond((a0, a1, a2, a3, a4) -> value);
				return;
			case 2:
				t.mutThird((a0, a1, a2, a3, a4) -> value);
				return;
			case 3:
				t.mutFourth((a0, a1, a2, a3, a4) -> value);
				return;
			case 4:
				t.mutFifth((a0, a1, a2, a3, a4) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.mutFirst((a0, a1, a2, a3, a4, a5) -> value);
				return;
			case 1:
				t.mutSecond((a0, a1, a2, a3, a4, a5) -> value);
				return;
			case 2:
				t.mutThird((a0, a1, a2, a3, a4, a5) -> value);
				return;
			case 3:
				t.mutFourth((a0, a1, a2, a3, a4, a5) -> value);
				return;
			case 4:
				t.mutFifth((a0, a1, a2, a3, a4, a5) -> value);
				return;
			case 5:
				t.mutSixth((a0, a1, a2, a3, a4, a5) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.mutFirst((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			case 1:
				t.mutSecond((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			case 2:
				t.mutThird((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			case 3:
				t.mutFourth((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			case 4:
				t.mutFifth((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			case 5:
				t.mutSixth((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			case 6:
				t.mutSeventh((a0, a1, a2, a3, a4, a5, a6) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			switch (index) {
			case 0:
				t.mutFirst((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 1:
				t.mutSecond((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 2:
				t.mutThird((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 3:
				t.mutFourth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 4:
				t.mutFifth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 5:
				t.mutSixth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 6:
				t.mutSeventh((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			case 7:
				t.mutEighth((a0, a1, a2, a3, a4, a5, a6, a7) -> value);
				return;
			default:
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static Tuple indexedMap(final MutableTuple tuple, final int size, final int index, final String value) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			return t.map(index, ignored -> value);
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static Tuple mapAll(final MutableTuple tuple, final int size) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			return t.map((a0, a1) -> "map-all-0", (a0, a1) -> "map-all-1");
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			return t.map((a0, a1, a2) -> "map-all-0", (a0, a1, a2) -> "map-all-1", (a0, a1, a2) -> "map-all-2");
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3) -> "map-all-0",
					(a0, a1, a2, a3) -> "map-all-1",
					(a0, a1, a2, a3) -> "map-all-2",
					(a0, a1, a2, a3) -> "map-all-3");
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4) -> "map-all-0",
					(a0, a1, a2, a3, a4) -> "map-all-1",
					(a0, a1, a2, a3, a4) -> "map-all-2",
					(a0, a1, a2, a3, a4) -> "map-all-3",
					(a0, a1, a2, a3, a4) -> "map-all-4");
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4, a5) -> "map-all-0",
					(a0, a1, a2, a3, a4, a5) -> "map-all-1",
					(a0, a1, a2, a3, a4, a5) -> "map-all-2",
					(a0, a1, a2, a3, a4, a5) -> "map-all-3",
					(a0, a1, a2, a3, a4, a5) -> "map-all-4",
					(a0, a1, a2, a3, a4, a5) -> "map-all-5");
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4, a5, a6) -> "map-all-0",
					(a0, a1, a2, a3, a4, a5, a6) -> "map-all-1",
					(a0, a1, a2, a3, a4, a5, a6) -> "map-all-2",
					(a0, a1, a2, a3, a4, a5, a6) -> "map-all-3",
					(a0, a1, a2, a3, a4, a5, a6) -> "map-all-4",
					(a0, a1, a2, a3, a4, a5, a6) -> "map-all-5",
					(a0, a1, a2, a3, a4, a5, a6) -> "map-all-6");
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-0",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-1",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-2",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-3",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-4",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-5",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-6",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "map-all-7");
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static Object scalarMap(final MutableTuple tuple, final int size) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			return t.map((a0, a1) -> "scalar-result");
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			return t.map((a0, a1, a2) -> "scalar-result");
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3) -> "scalar-result");
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4) -> "scalar-result");
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4, a5) -> "scalar-result");
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4, a5, a6) -> "scalar-result");
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			return t.map((a0, a1, a2, a3, a4, a5, a6, a7) -> "scalar-result");
		}
		default:
			throw new AssertionError(size);
		}
	}

	private static void mutAll(final MutableTuple tuple, final int size) {
		switch (size) {
		case 2: {
			final MutablePair<String, String> t = (MutablePair<String, String>) tuple;
			t.mut((a0, a1) -> "mut-all-0", (a0, a1) -> "mut-all-1");
			return;
		}
		case 3: {
			final MutableTriplet<String, String, String> t = (MutableTriplet<String, String, String>) tuple;
			t.mut((a0, a1, a2) -> "mut-all-0", (a0, a1, a2) -> "mut-all-1", (a0, a1, a2) -> "mut-all-2");
			return;
		}
		case 4: {
			final MutableQuadruple<String, String, String, String> t = (MutableQuadruple<String, String, String, String>) tuple;
			t.mut((a0, a1, a2, a3) -> "mut-all-0",
					(a0, a1, a2, a3) -> "mut-all-1",
					(a0, a1, a2, a3) -> "mut-all-2",
					(a0, a1, a2, a3) -> "mut-all-3");
			return;
		}
		case 5: {
			final MutableQuintuple<String, String, String, String, String> t = (MutableQuintuple<String, String, String, String, String>) tuple;
			t.mut((a0, a1, a2, a3, a4) -> "mut-all-0",
					(a0, a1, a2, a3, a4) -> "mut-all-1",
					(a0, a1, a2, a3, a4) -> "mut-all-2",
					(a0, a1, a2, a3, a4) -> "mut-all-3",
					(a0, a1, a2, a3, a4) -> "mut-all-4");
			return;
		}
		case 6: {
			final MutableSextuple<String, String, String, String, String, String> t = (MutableSextuple<String, String, String, String, String, String>) tuple;
			t.mut((a0, a1, a2, a3, a4, a5) -> "mut-all-0",
					(a0, a1, a2, a3, a4, a5) -> "mut-all-1",
					(a0, a1, a2, a3, a4, a5) -> "mut-all-2",
					(a0, a1, a2, a3, a4, a5) -> "mut-all-3",
					(a0, a1, a2, a3, a4, a5) -> "mut-all-4",
					(a0, a1, a2, a3, a4, a5) -> "mut-all-5");
			return;
		}
		case 7: {
			final MutableSeptuple<String, String, String, String, String, String, String> t = (MutableSeptuple<String, String, String, String, String, String, String>) tuple;
			t.mut((a0, a1, a2, a3, a4, a5, a6) -> "mut-all-0",
					(a0, a1, a2, a3, a4, a5, a6) -> "mut-all-1",
					(a0, a1, a2, a3, a4, a5, a6) -> "mut-all-2",
					(a0, a1, a2, a3, a4, a5, a6) -> "mut-all-3",
					(a0, a1, a2, a3, a4, a5, a6) -> "mut-all-4",
					(a0, a1, a2, a3, a4, a5, a6) -> "mut-all-5",
					(a0, a1, a2, a3, a4, a5, a6) -> "mut-all-6");
			return;
		}
		case 8: {
			final MutableOctuple<String, String, String, String, String, String, String, String> t = (MutableOctuple<String, String, String, String, String, String, String, String>) tuple;
			t.mut((a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-0",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-1",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-2",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-3",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-4",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-5",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-6",
					(a0, a1, a2, a3, a4, a5, a6, a7) -> "mut-all-7");
			return;
		}
		default:
			throw new AssertionError(size);
		}
	}

}
