package lu.pcy113.pclib.pointer.prim;

import java.util.Objects;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class ShortPointer extends PrimitivePointer<Short> {

	private short value;

	public ShortPointer() {
	}

	public ShortPointer(short value) {
		this.value = value;
	}

	public synchronized short getValue() {
		return this.value;
	}

	public synchronized void setValue(short value) {
		set(value);
	}

	public synchronized short increment() {
		return add((short) 1);
	}

	public synchronized short decrement() {
		return sub((short) 1);
	}

	public synchronized short add(short other) {
		return set(i -> (short) (i + other)).get();
	}

	public synchronized short mul(short other) {
		return set(i -> (short) (i * other)).get();
	}

	public synchronized short sub(short other) {
		return set(i -> (short) (i - other)).get();
	}

	public synchronized short div(short other) {
		return set(i -> (short) (i / other)).get();
	}

	public synchronized short mod(short other) {
		return set(i -> (short) (i % other)).get();
	}

	@Override
	public ObjectPointer<Short> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Short get() {
		return value;
	}

	@Override
	public synchronized ShortPointer set(Short value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
