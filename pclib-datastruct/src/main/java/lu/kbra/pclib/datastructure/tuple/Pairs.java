package lu.kbra.pclib.datastructure.tuple;

import java.util.List;
import java.util.function.Supplier;

public final class Pairs {

	public static <K, V> EditablePair<K, V> empty() {
		return new EditablePair<>();
	}

	public static <K, V> EditablePair<K, V> pair(final K key, final V value) {
		return new EditablePair<>(key, value);
	}

	public static <K, V> ReadOnlyPair<K, V> readOnly(final K key, final V value) {
		return new ReadOnlyPair<>(key, value);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B> List<EditablePair<A, B>>
			toPairList(final Supplier<List<EditablePair<A, B>>> listSupplier, final Object... objects) {
		final List<EditablePair<A, B>> list = listSupplier.get();

		if (objects.length % 2 != 0) {
			throw new IllegalArgumentException("Object count should be a multiple of 2.");
		}

		for (int i = 0; i < objects.length; i += 2) {
			list.add(new EditablePair<>((A) objects[i], (B) objects[i + 1]));
		}

		return list;
	}

	private Pairs() {
	}

}
