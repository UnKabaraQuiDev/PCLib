package lu.pcy113.pclib;

public class ReadOnlyTriplet<A, B, C> extends Triplet<A, B, C> {

	public ReadOnlyTriplet() {
		super();
	}

	public ReadOnlyTriplet(A first, B second, C third) {
		super(first, second, third);
	}

	@Override
	@Deprecated
	public void setFirst(A first) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	@Deprecated
	public void setSecond(B second) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	@Deprecated
	public void setThird(C third) {
		throw new UnsupportedOperationException("Operation not permitted on readonly triplet !");
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}