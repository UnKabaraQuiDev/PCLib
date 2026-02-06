package lu.pcy113.pclib.pointer.prim;

import java.util.Objects;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class LongPointer extends PrimitivePointer<Long> {

	private long value;

	public LongPointer() {
		this.value = 0L;
	}

	public LongPointer(long value) {
		this.value = value;
	}

	public synchronized long getValue() {
		return this.value;
	}

	public synchronized void setValue(long value) {
		set(value);
	}

	public synchronized long increment() {
		return add((long) 1);
	}

	public synchronized long decrement() {
		return sub((long) 1);
	}

	public synchronized long add(long other) {
		return set(i -> i + other).get();
	}

	public synchronized long mul(long other) {
		return set(i -> i * other).get();
	}

	public synchronized long sub(long other) {
		return set(i -> i - other).get();
	}

	public synchronized long div(long other) {
		return set(i -> i / other).get();
	}

	public synchronized long mod(long other) {
		return set(i -> i % other).get();
	}

	@Override
	public ObjectPointer<Long> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Long get() {
		return value;
	}

	@Override
	public synchronized LongPointer set(Long value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
