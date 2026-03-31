package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class ShortPointer extends PrimitivePointer<Short> {

	private short value;

	public ShortPointer() {
	}

	public ShortPointer(final short value) {
		this.value = value;
	}

	public synchronized short getValue() {
		return this.value;
	}

	public synchronized void setValue(final short value) {
		this.set(value);
	}

	public synchronized short increment() {
		return this.add((short) 1);
	}

	public synchronized short decrement() {
		return this.sub((short) 1);
	}

	public synchronized short add(final short other) {
		return this.set(i -> (short) (i + other)).get();
	}

	public synchronized short mul(final short other) {
		return this.set(i -> (short) (i * other)).get();
	}

	public synchronized short sub(final short other) {
		return this.set(i -> (short) (i - other)).get();
	}

	public synchronized short div(final short other) {
		return this.set(i -> (short) (i / other)).get();
	}

	public synchronized short mod(final short other) {
		return this.set(i -> (short) (i % other)).get();
	}

	@Override
	public ObjectPointer<Short> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Short get() {
		return this.value;
	}

	@Override
	public synchronized ShortPointer set(final Short value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
