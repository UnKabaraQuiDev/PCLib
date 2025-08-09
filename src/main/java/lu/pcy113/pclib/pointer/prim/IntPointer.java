package lu.pcy113.pclib.pointer.prim;

import java.util.Objects;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class IntPointer extends PrimitivePointer<Integer> {

	private int value;

	public IntPointer() {
		this.value = 0;
	}

	public IntPointer(int value) {
		this.value = value;
	}

	public synchronized int getValue() {
		return this.value;
	}

	public synchronized void setValue(int value) {
		set(value);
	}

	public synchronized int increment() {
		return add((int) 1);
	}

	public synchronized int decrement() {
		return sub((int) 1);
	}

	public synchronized int add(int other) {
		return set((int) (get() + other)).get();
	}

	public synchronized int mul(int other) {
		return set((int) (get() * other)).get();
	}

	public synchronized int sub(int other) {
		return set((int) (get() - other)).get();
	}

	public synchronized int div(int other) {
		return set((int) (get() / other)).get();
	}

	public synchronized int mod(int other) {
		return set((int) (get() % other)).get();
	}

	@Override
	public ObjectPointer<Integer> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Integer get() {
		return value;
	}

	@Override
	public synchronized IntPointer set(Integer value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
