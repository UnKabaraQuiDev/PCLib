package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Tuples {

	public static <K, V> MutablePair<K, V> emptyPair() {
		return new MutablePairImpl<>();
	}

	public static <K, V> MutablePair<K, V> mutable(final K key, final V value) {
		return new MutablePairImpl<>(key, value);
	}

	public static <K, V> ReadOnlyPair<K, V> readOnly(final K key, final V value) {
		return new ReadOnlyPairImpl<>(key, value);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B> List<MutablePair<A, B>>
			toPairList(final Supplier<List<MutablePair<A, B>>> listSupplier, final Object... objects) {
		final List<MutablePair<A, B>> list = listSupplier.get();

		if (objects.length % 2 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 2.");
		}

		for (int i = 0; i < objects.length; i += 2) {
			list.add(new MutablePairImpl<>((A) objects[i], (B) objects[i + 1]));
		}

		return list;
	}

	public static MutableTriplet<Object, Object, Object> emptyTriplet() {
		return new MutableTripletImpl<>();
	}

	public static <A, B, C> ReadOnlyTriplet<A, B, C> readOnly(final A first, final B second, final C third) {
		return new ReadOnlyTripletImpl<>(first, second, third);
	}

	public static <A, B, C> MutableTriplet<A, B, C> mutable(final A first, final B second, final C third) {
		return new MutableTripletImpl<>(first, second, third);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C> List<MutableTriplet<A, B, C>>
			toTripletList(final Supplier<List<MutableTriplet<A, B, C>>> listSupplier, final Object... objects) {
		final List<MutableTriplet<A, B, C>> list = listSupplier.get();

		if (objects.length % 3 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 3.");
		}

		for (int i = 0; i < objects.length; i += 3) {
			list.add(new MutableTripletImpl<>((A) objects[i], (B) objects[i + 1], (C) objects[i + 2]));
		}

		return list;
	}

	public static MutableQuadruple<Object, Object, Object, Object> emptyQuadruple() {
		return new MutableQuadrupleImpl<>();
	}

	public static <A, B, C, D> MutableQuadruple<A, B, C, D> mutable(final A first, final B second, final C third, final D fourth) {
		return new MutableQuadrupleImpl<>(first, second, third, fourth);
	}

	public static <A, B, C, D> ReadOnlyQuadruple<A, B, C, D> readOnly(final A first, final B second, final C third, final D fourth) {
		return new ReadOnlyQuadrupleImpl<>(first, second, third, fourth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D> List<MutableQuadruple<A, B, C, D>>
			toQuadrupleList(final Supplier<List<MutableQuadruple<A, B, C, D>>> listSupplier, final Object... objects) {
		final List<MutableQuadruple<A, B, C, D>> list = listSupplier.get();

		if (objects.length % 4 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 4.");
		}

		for (int i = 0; i < objects.length; i += 4) {
			list.add(new MutableQuadrupleImpl<>((A) objects[i], (B) objects[i + 1], (C) objects[i + 2], (D) objects[i + 3]));
		}

		return list;
	}

	public static MutableQuintuple<Object, Object, Object, Object, Object> emptyQuintuple() {
		return new MutableQuintupleImpl<>();
	}

	public static <A, B, C, D, E> MutableQuintuple<A, B, C, D, E>
			mutableQuintuple(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new MutableQuintupleImpl<>(first, second, third, fourth, fifth);
	}

	public static <A, B, C, D, E> ReadOnlyQuintuple<A, B, C, D, E>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new ReadOnlyQuintupleImpl<>(first, second, third, fourth, fifth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E> List<MutableQuintuple<A, B, C, D, E>>
			toQuintupleList(final Supplier<List<MutableQuintuple<A, B, C, D, E>>> listSupplier, final Object... objects) {
		final List<MutableQuintuple<A, B, C, D, E>> list = listSupplier.get();

		if (objects.length % 5 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 5.");
		}

		for (int i = 0; i < objects.length; i += 5) {
			list.add(new MutableQuintupleImpl<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4]));
		}

		return list;
	}

	public static MutableSextuple<Object, Object, Object, Object, Object, Object> emptySextuple() {
		return new MutableSextupleImpl<>();
	}

	public static <A, B, C, D, E, F> ReadOnlySextuple<A, B, C, D, E, F>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new ReadOnlySextupleImpl<>(first, second, third, fourth, fifth, sixth);
	}

	public static <A, B, C, D, E, F> MutableSextuple<A, B, C, D, E, F>
			mutableSextuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new MutableSextupleImpl<>(first, second, third, fourth, fifth, sixth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E, F> List<MutableSextuple<A, B, C, D, E, F>>
			toSextupleList(final Supplier<List<MutableSextuple<A, B, C, D, E, F>>> listSupplier, final Object... objects) {
		final List<MutableSextuple<A, B, C, D, E, F>> list = listSupplier.get();

		if (objects.length % 6 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 6.");
		}

		for (int i = 0; i < objects.length; i += 6) {
			list.add(new MutableSextupleImpl<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4],
					(F) objects[i + 5]));
		}

		return list;
	}

	public static MutableSeptuple<Object, Object, Object, Object, Object, Object, Object> emptySeptuple() {
		return new MutableSeptupleImpl<>();
	}

	public static <A, B, C, D, E, F, G> ReadOnlySeptuple<A, B, C, D, E, F, G>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new ReadOnlySeptupleImpl<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	public static <A, B, C, D, E, F, G> MutableSeptuple<A, B, C, D, E, F, G>
			mutableSeptuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new MutableSeptupleImpl<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E, F, G> List<MutableSeptuple<A, B, C, D, E, F, G>>
			toSeptupleList(final Supplier<List<MutableSeptuple<A, B, C, D, E, F, G>>> listSupplier, final Object... objects) {
		final List<MutableSeptuple<A, B, C, D, E, F, G>> list = listSupplier.get();

		if (objects.length % 7 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 7.");
		}

		for (int i = 0; i < objects.length; i += 7) {
			list.add(new MutableSeptupleImpl<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4],
					(F) objects[i + 5],
					(G) objects[i + 6]));
		}

		return list;
	}

	public static MutableOctuple<Object, Object, Object, Object, Object, Object, Object, Object> emptyOctuple() {
		return new MutableOctupleImpl<>();
	}

	public static <A, B, C, D, E, F, G, H> MutableOctuple<A, B, C, D, E, F, G, H> mutable(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		return new MutableOctupleImpl<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	public static <A, B, C, D, E, F, G, H> ReadOnlyOctuple<A, B, C, D, E, F, G, H> readOnly(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		return new ReadOnlyOctupleImpl<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E, F, G, H> List<MutableOctuple<A, B, C, D, E, F, G, H>>
			toOctupleList(final Supplier<List<MutableOctuple<A, B, C, D, E, F, G, H>>> listSupplier, final Object... objects) {
		final List<MutableOctuple<A, B, C, D, E, F, G, H>> list = listSupplier.get();

		if (objects.length % 8 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 8.");
		}

		for (int i = 0; i < objects.length; i += 8) {
			list.add(new MutableOctupleImpl<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4],
					(F) objects[i + 5],
					(G) objects[i + 6],
					(H) objects[i + 7]));
		}

		return list;
	}

	public static <K, V> MutablePair<K, V> synchronized_(final K key, final V value) {
		return new MutablePairSyncImpl<>(key, value);
	}

	public static <A, B, C> MutableTriplet<A, B, C> synchronized_(final A first, final B second, final C third) {
		return new MutableTripletSyncImpl<>(first, second, third);
	}

	public static <A, B, C, D> MutableQuadruple<A, B, C, D> synchronized_(final A first, final B second, final C third, final D fourth) {
		return new MutableQuadrupleSyncImpl<>(first, second, third, fourth);
	}

	public static <A, B, C, D, E> MutableQuintuple<A, B, C, D, E>
			synchronized_(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new MutableQuintupleSyncImpl<>(first, second, third, fourth, fifth);
	}

	public static <A, B, C, D, E, F> MutableSextuple<A, B, C, D, E, F>
			synchronized_(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new MutableSextupleSyncImpl<>(first, second, third, fourth, fifth, sixth);
	}

	public static <A, B, C, D, E, F, G> MutableSeptuple<A, B, C, D, E, F, G>
			synchronized_(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new MutableSeptupleSyncImpl<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	public static <A, B, C, D, E, F, G, H> MutableOctuple<A, B, C, D, E, F, G, H> synchronized_(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		return new MutableOctupleSyncImpl<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

}
