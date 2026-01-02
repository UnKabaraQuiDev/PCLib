package lu.pcy113.pclib.pointer.prim;

import java.util.Objects;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class BytePointer extends PrimitivePointer<Byte> {

	private byte value;

	public BytePointer() {
	}

	public BytePointer(byte value) {
		this.value = value;
	}

	public synchronized byte getValue() {
		return this.value;
	}

	public synchronized void setValue(byte value) {
		set(value);
	}

	public synchronized byte increment() {
		return add((byte) 1);
	}

	public synchronized byte decrement() {
		return sub((byte) 1);
	}

	public synchronized byte add(byte other) {
		return set(i -> (byte) (i + other)).get();
	}

	public synchronized byte mul(byte other) {
		return set(i -> (byte) (i * other)).get();
	}

	public synchronized byte sub(byte other) {
		return set(i -> (byte) (i - other)).get();
	}

	public synchronized byte div(byte other) {
		return set(i -> (byte) (i / other)).get();
	}

	public synchronized byte mod(byte other) {
		return set(i -> (byte) (i % other)).get();
	}

	@Override
	public ObjectPointer<Byte> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Byte get() {
		return value;
	}

	@Override
	public synchronized BytePointer set(Byte value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
