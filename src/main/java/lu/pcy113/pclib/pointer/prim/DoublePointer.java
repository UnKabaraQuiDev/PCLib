package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class DoublePointer extends PrimitivePointer<Double> {

	private double value;

	public DoublePointer() {
	}

	public DoublePointer(double value) {
		this.value = value;
	}

	public double getValue() {
		return this.value;
	}

	public synchronized void setValue(double value) {
		this.value = value;
	}

	public synchronized double increment() {
		return ++this.value;
	}

	public synchronized double decrement() {
		return ++this.value;
	}

	public synchronized double add(double other) {
		this.value += other;
		return value;
	}

	public synchronized double mul(double other) {
		this.value *= other;
		return value;
	}

	public synchronized double sub(double other) {
		this.value -= other;
		return value;
	}

	public synchronized double div(double other) {
		this.value /= other;
		return value;
	}

	public synchronized double mod(double other) {
		this.value %= other;
		return value;
	}

	@Override
	public ObjectPointer<Double> toObjectPointer() {
		return new ObjectPointer<Double>(this.value);
	}

}
