package lu.pcy113.pclib;

public class Triplet<A, B, C> {

	private A first;
	private B second;
	private C third;

	public Triplet() {
		this(null, null, null);
	}

	public Triplet(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public A getFirst() {
		return first;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public B getSecond() {
		return second;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public C getThird() {
		return third;
	}

	public void setThird(C third) {
		this.third = third;
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s}", first, second, third);
	}

	@Override
	protected Triplet<A, B, C> clone() {
		return new Triplet<>(first, second, third);
	}

}
