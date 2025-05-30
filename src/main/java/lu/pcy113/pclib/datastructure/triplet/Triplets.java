package lu.pcy113.pclib.datastructure.triplet;

public final class Triplets {

	public static final Triplet<Object, Object, Object> empty() {
		return new Triplet<Object, Object, Object>();
	}

	public static final <A, B, C> Triplet<A, B, C> triplet(A first, B second, C third) {
		return new Triplet<A, B, C>(first, second, third);
	}

	public static final <A, B, C> ReadOnlyTriplet<A, B, C> readOnly(A first, B second, C third) {
		return new ReadOnlyTriplet<A, B, C>(first, second, third);
	}

}
