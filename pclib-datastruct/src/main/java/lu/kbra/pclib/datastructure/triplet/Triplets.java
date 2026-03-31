package lu.kbra.pclib.datastructure.triplet;

public final class Triplets {

	public static Triplet<Object, Object, Object> empty() {
		return new Triplet<>();
	}

	public static <A, B, C> Triplet<A, B, C> triplet(final A first, final B second, final C third) {
		return new Triplet<>(first, second, third);
	}

	public static <A, B, C> ReadOnlyTriplet<A, B, C> readOnly(final A first, final B second, final C third) {
		return new ReadOnlyTriplet<>(first, second, third);
	}

}
