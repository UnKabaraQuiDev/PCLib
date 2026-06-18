package lu.kbra.pclib.datastructure.tuple;

public final class Sextuples {

	private Sextuples() {
	}

	public static Sextuple<Object, Object, Object, Object, Object, Object> empty() {
		return new Sextuple<>();
	}

	public static <A, B, C, D, E, F> ReadOnlySextuple<A, B, C, D, E, F> readOnly(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new ReadOnlySextuple<>(first, second, third, fourth, fifth, sixth);
	}

	public static <A, B, C, D, E, F> Sextuple<A, B, C, D, E, F> sextuple(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		return new Sextuple<>(first, second, third, fourth, fifth, sixth);
	}

}
