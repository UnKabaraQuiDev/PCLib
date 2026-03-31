package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class IntPointer extends PrimitivePointer<Integer> {

	private int value;

	public IntPointer() {
		this.value = 0;
	}

	public IntPointer(final int value) {
		this.value = value;
	}

	public synchronized int getValue() {
		return this.value;
	}

	public synchronized void setValue(final int value) {
		this.set(value);
	}

	public synchronized int increment() {
		return this.add(1);
	}

	public synchronized int decrement() {
		return this.sub(1);
	}

	public synchronized int add(final int other) {
		return this.set(i -> i + other).get();
	}

	public synchronized int mul(final int other) {
		return this.set(i -> i * other).get();
	}

	public synchronized int sub(final int other) {
		return this.set(i -> i - other).get();
	}

	public synchronized int div(final int other) {
		return this.set(i -> i / other).get();
	}

	public synchronized int mod(final int other) {
		return this.set(i -> i % other).get();
	}

	@Override
	public ObjectPointer<Integer> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Integer get() {
		return this.value;
	}

	@Override
	public synchronized IntPointer set(final Integer value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
