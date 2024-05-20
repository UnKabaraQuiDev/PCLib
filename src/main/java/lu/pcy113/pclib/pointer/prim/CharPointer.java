package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class CharPointer
		extends
		PrimitivePointer<Character> {

	private char value;

	public CharPointer() {}

	public CharPointer(char value) {
		this.value = value;
	}

	public char getValue() { return this.value; }

	public void setValue(char value) { this.value = value; }

	@Override
	public ObjectPointer<Character> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
