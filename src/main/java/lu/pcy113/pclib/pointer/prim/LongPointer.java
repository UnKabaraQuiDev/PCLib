package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class LongPointer extends PrimitivePointer<Long> {

	private long value;

	public LongPointer() {
		this.value = 0L;
	}

	public LongPointer(long value) {
		this.value = value;
	}

	public long getValue() {
		return this.value;
	}

	public synchronized void setValue(long value) {
		this.value = value;
	}

	public synchronized long increment() {
		return ++this.value;
	}

	public synchronized long decrement() {
		return ++this.value;
	}

	public synchronized long add(long other) {
		this.value += other;
		return value;
	}

	public synchronized long mul(long other) {
		this.value *= other;
		return value;
	}

	public synchronized long sub(long other) {
		this.value -= other;
		return value;
	}

	public synchronized long div(long other) {
		this.value /= other;
		return value;
	}

	public synchronized long mod(long other) {
		this.value %= other;
		return value;
	}

	@Override
	public ObjectPointer<Long> toObjectPointer() {
		return new ObjectPointer<Long>(this.value);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[value=" + value + "]";
	}

}
