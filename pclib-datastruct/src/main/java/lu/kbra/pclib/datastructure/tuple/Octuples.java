package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

public final class Octuples {

	public static EditableOctuple<Object, Object, Object, Object, Object, Object, Object, Object> empty() {
		return new EditableOctuple<>();
	}

	public static <A, B, C, D, E, F, G, H> EditableOctuple<A, B, C, D, E, F, G, H> octuple(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		return new EditableOctuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
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
		return new ReadOnlyOctuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E, F, G, H> List<EditableOctuple<A, B, C, D, E, F, G, H>>
			toOctupleList(final Supplier<List<EditableOctuple<A, B, C, D, E, F, G, H>>> listSupplier, final Object... objects) {
		final List<EditableOctuple<A, B, C, D, E, F, G, H>> list = listSupplier.get();

		if (objects.length % 8 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 8.");
		}

		for (int i = 0; i < objects.length; i += 8) {
			list.add(new EditableOctuple<>((A) objects[i],
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

	private Octuples() {
	}

}
