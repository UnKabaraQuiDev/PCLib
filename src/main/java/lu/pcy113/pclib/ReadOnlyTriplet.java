package lu.pcy113.pclib;

public class ReadOnlyTriplet<A, B, C> extends Triplet<A, B, C> {

	public ReadOnlyTriplet() {
		super();
	}

	public ReadOnlyTriplet(A first, B second, C third) {
		super(first, second, third);
	}

	@Override
	public void setFirst(A first) {
		throw new RuntimeException(new IllegalAccessException("Operation not permitted on readonly triplet !"));
	}

	@Override
	public void setSecond(B second) {
		throw new RuntimeException(new IllegalAccessException("Operation not permitted on readonly triplet !"));
	}

	@Override
	public void setThird(C third) {
		throw new RuntimeException(new IllegalAccessException("Operation not permitted on readonly triplet !"));
	}

	@Override
	public String toString() {
		return String.format("%s(readonly)", super.toString());
	}

}