package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

public final class Quadruples {

	public static EditableQuadruple<Object, Object, Object, Object> empty() {
		return new EditableQuadruple<>();
	}

	public static <A, B, C, D> EditableQuadruple<A, B, C, D> quadruple(final A first, final B second, final C third, final D fourth) {
		return new EditableQuadruple<>(first, second, third, fourth);
	}

	public static <A, B, C, D> ReadOnlyQuadruple<A, B, C, D> readOnly(final A first, final B second, final C third, final D fourth) {
		return new ReadOnlyQuadruple<>(first, second, third, fourth);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C, D> List<EditableQuadruple<A, B, C, D>>
			toQuadrupleList(final Supplier<List<EditableQuadruple<A, B, C, D>>> listSupplier, final Object... objects) {
		final List<EditableQuadruple<A, B, C, D>> list = listSupplier.get();

		if (objects.length % 4 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 4.");
		}

		for (int i = 0; i < objects.length; i += 4) {
			list.add(new EditableQuadruple<>((A) objects[i], (B) objects[i + 1], (C) objects[i + 2], (D) objects[i + 3]));
		}

		return list;
	}

	private Quadruples() {
	}

}
