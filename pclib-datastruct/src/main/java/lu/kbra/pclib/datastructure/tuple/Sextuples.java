package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Sextuples {

	@Deprecated
	public static MutableSextuple<Object, Object, Object, Object, Object, Object> empty() {
		return new MutableSextupleImpl<>();
	}

	@Deprecated
	public static <A, B, C, D, E, F> ReadOnlySextuple<A, B, C, D, E, F>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new ReadOnlySextupleImpl<>(first, second, third, fourth, fifth, sixth);
	}

	@Deprecated
	public static <A, B, C, D, E, F> MutableSextuple<A, B, C, D, E, F>
			sextuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new MutableSextupleImpl<>(first, second, third, fourth, fifth, sixth);
	}

	@Deprecated
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
}
