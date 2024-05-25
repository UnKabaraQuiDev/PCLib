package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class CharPointer extends PrimitivePointer<Character> {

	private char value;

	public CharPointer() {
	}

	public CharPointer(char value) {
		this.value = value;
	}

	public char getValue() {
		return this.value;
	}

	public synchronized void setValue(char value) {
		this.value = value;
	}

	public synchronized char increment() {
		return ++this.value;
	}

	public synchronized char decrement() {
		return ++this.value;
	}

	public synchronized char add(char other) {
		this.value += other;
		return value;
	}

	public synchronized char mul(char other) {
		this.value *= other;
		return value;
	}

	public synchronized char sub(char other) {
		this.value -= other;
		return value;
	}

	public synchronized char div(char other) {
		this.value /= other;
		return value;
	}

	public synchronized char mod(char other) {
		this.value %= other;
		return value;
	}

	@Override
	public ObjectPointer<Character> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

}
