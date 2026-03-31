package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class DoublePointer extends PrimitivePointer<Double> {

	private double value;

	public DoublePointer() {
	}

	public DoublePointer(final double value) {
		this.value = value;
	}

	public synchronized double getValue() {
		return this.value;
	}

	public synchronized void setValue(final double value) {
		this.set(value);
	}

	public synchronized double increment() {
		return this.add(1);
	}

	public synchronized double decrement() {
		return this.sub(1);
	}

	public synchronized double add(final double other) {
		return this.set(i -> i + other).get();
	}

	public synchronized double mul(final double other) {
		return this.set(i -> i * other).get();
	}

	public synchronized double sub(final double other) {
		return this.set(i -> i - other).get();
	}

	public synchronized double div(final double other) {
		return this.set(i -> i / other).get();
	}

	public synchronized double mod(final double other) {
		return this.set(i -> i % other).get();
	}

	@Override
	public ObjectPointer<Double> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Double get() {
		return this.value;
	}

	@Override
	public synchronized DoublePointer set(final Double value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
