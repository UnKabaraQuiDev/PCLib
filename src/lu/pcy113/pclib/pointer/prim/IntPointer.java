package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class IntPointer extends PrimitivePointer<Integer> {

	private int value;

	public IntPointer() {
		this.value = 0;
	}

	public IntPointer(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public synchronized void setValue(int value) {
		this.value = value;
	}

	public synchronized int increment() {
		return ++this.value;
	}

	public synchronized int decrement() {
		return ++this.value;
	}

	public synchronized int add(int other) {
		this.value += other;
		return value;
	}

	public synchronized int mul(int other) {
		this.value *= other;
		return value;
	}

	public synchronized int sub(int other) {
		this.value -= other;
		return value;
	}

	public synchronized int div(int other) {
		this.value /= other;
		return value;
	}

	public synchronized int mod(int other) {
		this.value %= other;
		return value;
	}

	@Override
	public ObjectPointer<Integer> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
