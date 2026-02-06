package lu.pcy113.pclib.pointer.prim;

import java.util.Objects;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class CharPointer extends PrimitivePointer<Character> {

	private char value;

	public CharPointer() {
	}

	public CharPointer(char value) {
		this.value = value;
	}

	public synchronized char getValue() {
		return this.value;
	}

	public synchronized void setValue(char value) {
		set(value);
	}

	public synchronized char increment() {
		return add((char) 1);
	}

	public synchronized char decrement() {
		return sub((char) 1);
	}

	public synchronized char add(char other) {
		return set(i -> (char) (i + other)).get();
	}

	public synchronized char mul(char other) {
		return set(i -> (char) (i * other)).get();
	}

	public synchronized char sub(char other) {
		return set(i -> (char) (i - other)).get();
	}

	public synchronized char div(char other) {
		return set(i -> (char) (i / other)).get();
	}

	public synchronized char mod(char other) {
		return set(i -> (char) (i % other)).get();
	}

	@Override
	public ObjectPointer<Character> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Character get() {
		return value;
	}

	@Override
	public synchronized CharPointer set(Character value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
