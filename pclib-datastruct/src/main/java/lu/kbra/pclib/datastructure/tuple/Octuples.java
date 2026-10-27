package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Octuples {

	@Deprecated
	public static MutableOctuple<Object, Object, Object, Object, Object, Object, Object, Object> empty() {
		return new MutableOctupleImpl<>();
	}

	@Deprecated
	public static <A, B, C, D, E, F, G, H> MutableOctuple<A, B, C, D, E, F, G, H> octuple(
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

	@Deprecated
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

	@Deprecated
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
}
