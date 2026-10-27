package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@Deprecated
@UtilityClass
public class Pairs {

	@Deprecated
	public static <K, V> MutablePair<K, V> empty() {
		return new MutablePairImpl<>();
	}

	@Deprecated
	public static <K, V> MutablePair<K, V> pair(final K key, final V value) {
		return new MutablePairImpl<>(key, value);
	}

	@Deprecated
	public static <K, V> ReadOnlyPair<K, V> readOnly(final K key, final V value) {
		return new ReadOnlyPairImpl<>(key, value);
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public static final <A, B> List<MutablePair<A, B>>
			toPairList(final Supplier<List<MutablePair<A, B>>> listSupplier, final Object... objects) {
		final List<MutablePair<A, B>> list = listSupplier.get();

		if (objects.length % 2 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 2.");
		}

		for (int i = 0; i < objects.length; i += 2) {
			list.add(new MutablePairImpl<>((A) objects[i], (B) objects[i + 1]));
		}

		return list;
	}
}
