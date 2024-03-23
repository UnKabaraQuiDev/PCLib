package lu.pcy113.pclib;

public class Triplet<A, B, C> extends ReadOnlyTriplet<A, B, C> {

	public Triplet() {
		this(null, null, null);
	}

	public Triplet(A first, B second, C third) {
		super(first, second, third);
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public void setThird(C third) {
		this.third = third;
	}

	@Override
	protected Triplet<A, B, C> clone() {
		return new Triplet<A, B, C>(first, second, third);
	}

}
