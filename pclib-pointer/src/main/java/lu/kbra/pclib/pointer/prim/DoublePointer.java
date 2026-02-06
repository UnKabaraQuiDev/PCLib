package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class DoublePointer extends PrimitivePointer<Double> {

	private double value;

	public DoublePointer() {
	}

	public DoublePointer(double value) {
		this.value = value;
	}

	public synchronized double getValue() {
		return this.value;
	}

	public synchronized void setValue(double value) {
		set(value);
	}

	public synchronized double increment() {
		return add((double) 1);
	}

	public synchronized double decrement() {
		return sub((double) 1);
	}

	public synchronized double add(double other) {
		return set(i -> i + other).get();
	}

	public synchronized double mul(double other) {
		return set(i -> i * other).get();
	}

	public synchronized double sub(double other) {
		return set(i -> i - other).get();
	}

	public synchronized double div(double other) {
		return set(i -> i / other).get();
	}

	public synchronized double mod(double other) {
		return set(i -> i % other).get();
	}

	@Override
	public ObjectPointer<Double> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Double get() {
		return value;
	}

	@Override
	public synchronized DoublePointer set(Double value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
