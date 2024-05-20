package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class BytePointer
		extends
		PrimitivePointer<Byte> {

	private byte value;

	public BytePointer() {}

	public BytePointer(byte value) {
		this.value = value;
	}

	public byte getValue() { return this.value; }

	public void setValue(byte value) { this.value = value; }

	@Override
	public ObjectPointer<Byte> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
