package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

public final class Sextuples {

	public static EditableSextuple<Object, Object, Object, Object, Object, Object> empty() {
		return new EditableSextuple<>();
	}

	public static <A, B, C, D, E, F> ReadOnlySextuple<A, B, C, D, E, F>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new ReadOnlySextuple<>(first, second, third, fourth, fifth, sixth);
	}

	public static <A, B, C, D, E, F> EditableSextuple<A, B, C, D, E, F>
			sextuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new EditableSextuple<>(first, second, third, fourth, fifth, sixth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E, F> List<EditableSextuple<A, B, C, D, E, F>>
			toSextupleList(final Supplier<List<EditableSextuple<A, B, C, D, E, F>>> listSupplier, final Object... objects) {
		final List<EditableSextuple<A, B, C, D, E, F>> list = listSupplier.get();

		if (objects.length % 6 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 6.");
		}

		for (int i = 0; i < objects.length; i += 6) {
			list.add(new EditableSextuple<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4],
					(F) objects[i + 5]));
		}

		return list;
	}

	private Sextuples() {
	}

}
