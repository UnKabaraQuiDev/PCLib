package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class BooleanPointer extends PrimitivePointer<Boolean> {

	private boolean value;

	public BooleanPointer() {
	}

	public BooleanPointer(final boolean value) {
		this.value = value;
	}

	public synchronized boolean flip() {
		return this.value ^= true;
	}

	@Override
	public synchronized Boolean get() {
		return this.value;
	}

	public synchronized boolean getValue() {
		return this.get();
	}

	@Override
	public synchronized BooleanPointer set(final Boolean value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

	public synchronized void setValue(final boolean value) {
		this.set(value);
	}

	@Override
	public ObjectPointer<Boolean> toObjectPointer() {
		return new ObjectPointer<>(this.get());
	}

	@Override
	public String toString() {
		return "BooleanPointer [value=" + this.value + "]";
	}

	public synchronized boolean waitForFalse() {
		return super.waitForSet(v -> !(boolean) v);
	}

	public synchronized boolean waitForFalse(final long timeout) {
		return super.waitForSet(timeout, v -> !(boolean) v);
	}

	public synchronized boolean waitForTrue() {
		return super.waitForSet(v -> (boolean) v);
	}

	public synchronized boolean waitForTrue(final long timeout) {
		return super.waitForSet(timeout, v -> (boolean) v);
	}

}
