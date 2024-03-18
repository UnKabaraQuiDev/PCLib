package lu.pcy113.pclib;

public class ReadOnlyTriplet<A, B, C> {
	
	protected A first;
	protected B second;
	protected C third;

	public ReadOnlyTriplet() {
		this(null, null, null);
	}

	public ReadOnlyTriplet(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	public C getThird() {
		return third;
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s}", first, second, third);
	}

	@Override
	protected ReadOnlyTriplet<A, B, C> clone() {
		return new ReadOnlyTriplet<>(first, second, third);
	}
	
}
