package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class LongPointer
		extends
		PrimitivePointer {

	private long value;

	public LongPointer() {}

	public LongPointer(long value) {
		this.value = value;
	}

	public long getValue() { return this.value; }

	public void setValue(long value) { this.value = value; }

	@Override
	public ObjectPointer toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
