package lu.kbra.pclib.datastructure.tuple;

public final class Quintuples {

	private Quintuples() {
	}

	public static Quintuple<Object, Object, Object, Object, Object> empty() {
		return new Quintuple<>();
	}

	public static <A, B, C, D, E> ReadOnlyQuintuple<A, B, C, D, E> readOnly(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new ReadOnlyQuintuple<>(first, second, third, fourth, fifth);
	}

	public static <A, B, C, D, E> Quintuple<A, B, C, D, E> quintuple(final A first, final B second, final C third, final D fourth, final E fifth) {
		return new Quintuple<>(first, second, third, fourth, fifth);
	}

}
