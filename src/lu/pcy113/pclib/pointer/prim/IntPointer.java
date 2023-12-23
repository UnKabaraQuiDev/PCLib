package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class IntPointer
		extends
		PrimitivePointer<Integer> {

	private int value;

	public IntPointer() {}

	public IntPointer(int value) {
		this.value = value;
	}

	public int getValue() { return this.value; }

	public void setValue(int value) { this.value = value; }

	@Override
	public ObjectPointer<Integer> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
