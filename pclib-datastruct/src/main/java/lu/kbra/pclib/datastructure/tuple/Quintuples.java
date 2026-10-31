package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Quintuples {

	@Deprecated
	public static MutableQuintuple<Object, Object, Object, Object, Object> empty() {
		return new MutableQuintupleImpl<>();
	}

	@Deprecated
	public static <A, B, C, D, E> MutableQuintuple<A, B, C, D, E>
			quintuple(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new MutableQuintupleImpl<>(first, second, third, fourth, fifth);
	}

	@Deprecated
	public static <A, B, C, D, E> ReadOnlyQuintuple<A, B, C, D, E>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new ReadOnlyQuintupleImpl<>(first, second, third, fourth, fifth);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static final <A, B, C, D, E> List<MutableQuintuple<A, B, C, D, E>>
			toQuintupleList(final Supplier<List<MutableQuintuple<A, B, C, D, E>>> listSupplier, final Object... objects) {
		final List<MutableQuintuple<A, B, C, D, E>> list = listSupplier.get();

		if (objects.length % 5 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 5.");
		}

		for (int i = 0; i < objects.length; i += 5) {
			list.add(new MutableQuintupleImpl<>((A) objects[i],
					(B) objects[i + 1],
					(C) objects[i + 2],
					(D) objects[i + 3],
					(E) objects[i + 4]));
		}

		return list;
	}
}
