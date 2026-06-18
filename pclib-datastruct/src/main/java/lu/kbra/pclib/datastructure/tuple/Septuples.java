package lu.kbra.pclib.datastructure.tuple;

public final class Septuples {

	public static Septuple<Object, Object, Object, Object, Object, Object, Object> empty() {
		return new Septuple<>();
	}

	public static <A, B, C, D, E, F, G> ReadOnlySeptuple<A, B, C, D, E, F, G>
			readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new ReadOnlySeptuple<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	public static <A, B, C, D, E, F, G> Septuple<A, B, C, D, E, F, G>
			septuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth, final G seventh) {
		return new Septuple<>(first, second, third, fourth, fifth, sixth, seventh);
	}

	private Septuples() {
	}

}
