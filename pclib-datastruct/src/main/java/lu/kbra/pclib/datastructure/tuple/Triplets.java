package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

public final class Triplets {

	public static EditableTriplet<Object, Object, Object> empty() {
		return new EditableTriplet<>();
	}

	public static <A, B, C> ReadOnlyTriplet<A, B, C> readOnly(final A first, final B second, final C third) {
		return new ReadOnlyTriplet<>(first, second, third);
	}

	public static <A, B, C> EditableTriplet<A, B, C> triplet(final A first, final B second, final C third) {
		return new EditableTriplet<>(first, second, third);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C> List<EditableTriplet<A, B, C>>
			toTripletList(final Supplier<List<EditableTriplet<A, B, C>>> listSupplier, final Object... objects) {
		final List<EditableTriplet<A, B, C>> list = listSupplier.get();

		if (objects.length % 3 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 3.");
		}

		for (int i = 0; i < objects.length; i += 3) {
			list.add(new EditableTriplet<>((A) objects[i], (B) objects[i + 1], (C) objects[i + 2]));
		}

		return list;
	}

	private Triplets() {
	}

}
