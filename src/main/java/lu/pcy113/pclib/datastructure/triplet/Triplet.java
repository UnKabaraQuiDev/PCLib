package lu.pcy113.pclib.datastructure.triplet;

import java.util.function.Supplier;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.datastructure.tuple.Tuple;
import lu.pcy113.pclib.impl.DeepCloneable;

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
	public String toString() {
		return String.format("{%s,%s,%s}", first, second, third);
	}

	@Override
	public Triplet<A, B, C> clone() {
		return new Triplet<>(first, second, third);
	}

	@Override
	public Triplet<A, B, C> deepClone() {
		return new Triplet<A, B, C>((A) ((DeepCloneable) first).deepClone(), (B) ((DeepCloneable) second).deepClone(), (C) ((DeepCloneable) third).deepClone());
	}

	@Override
	public int elementCount() {
		return 3;
	}

	@Override
	public <T> T get(int i) {
		return i == 0 ? (T) first : i == 1 ? (T) second : (T) third;
	}

	@Override
	public Object[] asArray() {
		return new Object[] { first, second, third };
	}

	@Override
	public <T> T[] asArray(T[] arr) {
		return PCUtils.fillArray(arr, first, second, third);
	}

	@Override
	public <T> T[] asArray(Supplier<T[]> arr) {
		return PCUtils.fillArray(arr.get(), first, second, third);
	}

}
