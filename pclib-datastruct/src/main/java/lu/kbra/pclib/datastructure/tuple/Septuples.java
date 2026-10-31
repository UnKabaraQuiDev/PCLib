package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Septuples {

	@Deprecated
	public static MutableSeptuple<Object, Object, Object, Object, Object, Object, Object> empty() {
		return new MutableSeptupleImpl<>();
	}

	@Deprecated
	public static <A, B, C, D, E, F, G> ReadOnlySeptuple<A, B, C, D, E, F, G>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new ReadOnlySeptupleImpl<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	@Deprecated
	public static <A, B, C, D, E, F, G> MutableSeptuple<A, B, C, D, E, F, G>
			septuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new MutableSeptupleImpl<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	@Deprecated
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
}
