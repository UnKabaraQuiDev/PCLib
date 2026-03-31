package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class LongPointer extends PrimitivePointer<Long> {

	private long value;

	public LongPointer() {
		this.value = 0L;
	}

	public LongPointer(final long value) {
		this.value = value;
	}

	public synchronized long getValue() {
		return this.value;
	}

	public synchronized void setValue(final long value) {
		this.set(value);
	}

	public synchronized long increment() {
		return this.add(1);
	}

	public synchronized long decrement() {
		return this.sub(1);
	}

	public synchronized long add(final long other) {
		return this.set(i -> i + other).get();
	}

	public synchronized long mul(final long other) {
		return this.set(i -> i * other).get();
	}

	public synchronized long sub(final long other) {
		return this.set(i -> i - other).get();
	}

	public synchronized long div(final long other) {
		return this.set(i -> i / other).get();
	}

	public synchronized long mod(final long other) {
		return this.set(i -> i % other).get();
	}

	@Override
	public ObjectPointer<Long> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Long get() {
		return this.value;
	}

	@Override
	public synchronized LongPointer set(final Long value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
