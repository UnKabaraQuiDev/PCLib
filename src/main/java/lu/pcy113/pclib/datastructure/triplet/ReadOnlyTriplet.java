package lu.pcy113.pclib.datastructure.triplet;

public class ReadOnlyTriplet<A, B, C> extends Triplet<A, B, C> {

	public ReadOnlyTriplet() {
	}

	public ReadOnlyTriplet(A first, B second, C third) {
		super(first, second, third);
	}

	@Override
	@Deprecated
	public ReadOnlyTriplet<A, B, C> setFirst(A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	@Deprecated
	public ReadOnlyTriplet<A, B, C> setSecond(B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	@Deprecated
	public ReadOnlyTriplet<A, B, C> setThird(C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	public ReadOnlyTriplet<A, B, C> clone() {
		return (ReadOnlyTriplet<A, B, C>) super.clone();
	}
	
	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}