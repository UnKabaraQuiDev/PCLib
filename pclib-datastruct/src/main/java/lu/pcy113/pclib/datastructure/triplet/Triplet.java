package lu.pcy113.pclib.datastructure.triplet;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.pcy113.pclib.datastructure.DeepCloneable;
import lu.pcy113.pclib.datastructure.tuple.Tuple;

public class Triplet<A, B, C> implements DeepCloneable, Tuple {

	protected A first;
	protected B second;
	protected C third;

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

	public B getSecond() {
		return second;
	}

	public C getThird() {
		return third;
	}

	public Triplet<A, B, C> setFirst(A first) {
		this.first = first;
		return this;
	}

	public Triplet<A, B, C> setSecond(B second) {
		this.second = second;
		return this;
	}

	public Triplet<A, B, C> setThird(C third) {
		this.third = third;
		return this;
	}

	@Override
	public int elementCount() {
		return 3;
	}

	@Override
	public <T> T get(int i) {
		if (i < 0 || i > 2) {
			throw new IndexOutOfBoundsException(i + " <> [0..2]");
		}
		return i == 0 ? (T) first : i == 1 ? (T) second : (T) third;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { first, second, third };
	}

	@Override
	public Triplet<A, B, C> clone() {
		try {
			return (Triplet<A, B, C>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("{%s,%s,%s}", first, second, third);
	}

}
