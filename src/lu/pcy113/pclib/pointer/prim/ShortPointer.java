package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class ShortPointer
		extends
		PrimitivePointer {

	private short value;

	public ShortPointer() {}

	public ShortPointer(short value) {
		this.value = value;
	}

	public short getValue() { return this.value; }

	public void setValue(short value) { this.value = value; }

	@Override
	public ObjectPointer toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
