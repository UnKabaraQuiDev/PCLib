package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class BooleanPointer extends PrimitivePointer<Boolean> {

	private boolean value;

	public BooleanPointer() {
	}

	public BooleanPointer(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	public synchronized void setValue(boolean value) {
		this.value = value;
	}

	public synchronized boolean flip() {
		return this.value ^= true;
	}

	@Override
	public ObjectPointer<Boolean> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
