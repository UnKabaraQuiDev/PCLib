package lu.kbra.pclib.datastructure.tuple;

public final class Quadruples {

	private Quadruples() {
	}

	public static Quadruple<Object, Object, Object, Object> empty() {
		return new Quadruple<>();
	}

	public static <A, B, C, D> ReadOnlyQuadruple<A, B, C, D> readOnly(final A first, final B second, final C third, final D fourth) {
		return new ReadOnlyQuadruple<>(first, second, third, fourth);
	}

	public static <A, B, C, D> Quadruple<A, B, C, D> quadruple(final A first, final B second, final C third, final D fourth) {
		return new Quadruple<>(first, second, third, fourth);
	}

}
