package lu.kbra.pclib.datastructure.tuple;

public final class Octuples {

	private Octuples() {
	}

	public static Octuple<Object, Object, Object, Object, Object, Object, Object, Object> empty() {
		return new Octuple<>();
	}

	public static <A, B, C, D, E, F, G, H> ReadOnlyOctuple<A, B, C, D, E, F, G, H> readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh, final H eighth) {
		return new ReadOnlyOctuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	public static <A, B, C, D, E, F, G, H> Octuple<A, B, C, D, E, F, G, H> octuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh, final H eighth) {
		return new Octuple<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

}
