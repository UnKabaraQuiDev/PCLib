package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class BytePointer extends PrimitivePointer<Byte> {

	private byte value;

	public BytePointer() {
	}

	public BytePointer(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return this.value;
	}

	public synchronized void setValue(byte value) {
		this.value = value;
	}

	public synchronized byte increment() {
		return ++this.value;
	}

	public synchronized byte decrement() {
		return ++this.value;
	}

	public synchronized byte add(byte other) {
		this.value += other;
		return value;
	}

	public synchronized byte mul(byte other) {
		this.value *= other;
		return value;
	}

	public synchronized byte sub(byte other) {
		this.value -= other;
		return value;
	}

	public synchronized byte div(byte other) {
		this.value /= other;
		return value;
	}

	public synchronized byte mod(byte other) {
		this.value %= other;
		return value;
	}

	@Override
	public ObjectPointer<Byte> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
