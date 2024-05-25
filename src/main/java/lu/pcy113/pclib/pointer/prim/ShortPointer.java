package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class ShortPointer extends PrimitivePointer<Short> {

	private short value;

	public ShortPointer() {
	}

	public ShortPointer(short value) {
		this.value = value;
	}

	public short getValue() {
		return this.value;
	}

	public synchronized void setValue(short value) {
		this.value = value;
	}

	public synchronized short increment() {
		return ++this.value;
	}

	public synchronized short decrement() {
		return ++this.value;
	}

	public synchronized short add(short other) {
		this.value += other;
		return value;
	}

	public synchronized short mul(short other) {
		this.value *= other;
		return value;
	}

	public synchronized short sub(short other) {
		this.value -= other;
		return value;
	}

	public synchronized short div(short other) {
		this.value /= other;
		return value;
	}

	public synchronized short mod(short other) {
		this.value %= other;
		return value;
	}

	@Override
	public ObjectPointer<Short> toObjectPointer() {
		return new ObjectPointer<Short>(this.value);
	}

}
