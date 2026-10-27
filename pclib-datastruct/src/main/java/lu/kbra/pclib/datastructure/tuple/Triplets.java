package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Triplets {

	@Deprecated
	public static MutableTriplet<Object, Object, Object> empty() {
		return new MutableTripletImpl<>();
	}

	@Deprecated
	public static <A, B, C> ReadOnlyTriplet<A, B, C> readOnly(final A first, final B second, final C third) {
		return new ReadOnlyTripletImpl<>(first, second, third);
	}

	@Deprecated
	public static <A, B, C> MutableTriplet<A, B, C> triplet(final A first, final B second, final C third) {
		return new MutableTripletImpl<>(first, second, third);
	}

	@Deprecated
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
}
