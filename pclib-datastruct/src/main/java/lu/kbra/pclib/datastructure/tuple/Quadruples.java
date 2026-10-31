package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Quadruples {

	@Deprecated
	public static MutableQuadruple<Object, Object, Object, Object> empty() {
		return new MutableQuadrupleImpl<>();
	}

	@Deprecated
	public static <A, B, C, D> MutableQuadruple<A, B, C, D> quadruple(final A first, final B second, final C third, final D fourth) {
		return new MutableQuadrupleImpl<>(first, second, third, fourth);
	}

	@Deprecated
	public static <A, B, C, D> ReadOnlyQuadruple<A, B, C, D> readOnly(final A first, final B second, final C third, final D fourth) {
		return new ReadOnlyQuadrupleImpl<>(first, second, third, fourth);
	}

	@Deprecated
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
}
