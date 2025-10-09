package lu.pcy113.pclib.pointer.prim;

import java.util.Objects;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class BooleanPointer extends PrimitivePointer<Boolean> {

	private boolean value;

	public BooleanPointer() {
	}

	public BooleanPointer(boolean value) {
		this.value = value;
	}

	public synchronized boolean waitForTrue() {
		return super.waitForChange((v) -> (boolean) v);
	}

	public synchronized boolean waitForTrue(long timeout) {
		return super.waitForChange(timeout, (v) -> (boolean) v);
	}

	public synchronized boolean waitForFalse() {
		return super.waitForChange((v) -> !(boolean) v);
	}

	public synchronized boolean waitForFalse(long timeout) {
		return super.waitForChange(timeout, (v) -> !(boolean) v);
	}

	public synchronized boolean getValue() {
		return get();
	}

	public synchronized void setValue(boolean value) {
		set(value);
	}

	public synchronized boolean flip() {
		return this.value ^= true;
	}

	@Override
	public ObjectPointer<Boolean> toObjectPointer() {
		return new ObjectPointer<>(get());
	}

	@Override
	public synchronized Boolean get() {
		return value;
	}

	@Override
	public synchronized BooleanPointer set(Boolean value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
