package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

public final class Quintuples {

	public static EditableQuintuple<Object, Object, Object, Object, Object> empty() {
		return new EditableQuintuple<>();
	}

	public static <A, B, C, D, E> EditableQuintuple<A, B, C, D, E>
			quintuple(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new EditableQuintuple<>(first, second, third, fourth, fifth);
	}

	public static <A, B, C, D, E> ReadOnlyQuintuple<A, B, C, D, E>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new ReadOnlyQuintuple<>(first, second, third, fourth, fifth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E> List<EditableQuintuple<A, B, C, D, E>>
			toQuintupleList(final Supplier<List<EditableQuintuple<A, B, C, D, E>>> listSupplier, final Object... objects) {
		final List<EditableQuintuple<A, B, C, D, E>> list = listSupplier.get();

		if (objects.length % 5 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 5.");
		}

		for (int i = 0; i < objects.length; i += 5) {
			list.add(new EditableQuintuple<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4]));
		}

		return list;
	}

	private Quintuples() {
	}

}
