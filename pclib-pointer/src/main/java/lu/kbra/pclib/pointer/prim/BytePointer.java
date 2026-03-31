package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class BytePointer extends PrimitivePointer<Byte> {

	private byte value;

	public BytePointer() {
	}

	public BytePointer(final byte value) {
		this.value = value;
	}

	public synchronized byte getValue() {
		return this.value;
	}

	public synchronized void setValue(final byte value) {
		this.set(value);
	}

	public synchronized byte increment() {
		return this.add((byte) 1);
	}

	public synchronized byte decrement() {
		return this.sub((byte) 1);
	}

	public synchronized byte add(final byte other) {
		return this.set(i -> (byte) (i + other)).get();
	}

	public synchronized byte mul(final byte other) {
		return this.set(i -> (byte) (i * other)).get();
	}

	public synchronized byte sub(final byte other) {
		return this.set(i -> (byte) (i - other)).get();
	}

	public synchronized byte div(final byte other) {
		return this.set(i -> (byte) (i / other)).get();
	}

	public synchronized byte mod(final byte other) {
		return this.set(i -> (byte) (i % other)).get();
	}

	@Override
	public ObjectPointer<Byte> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Byte get() {
		return this.value;
	}

	@Override
	public synchronized BytePointer set(final Byte value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
