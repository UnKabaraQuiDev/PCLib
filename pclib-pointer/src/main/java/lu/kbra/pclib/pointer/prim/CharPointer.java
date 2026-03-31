package lu.kbra.pclib.pointer.prim;

import java.util.Objects;

import lu.kbra.pclib.pointer.ObjectPointer;

public class CharPointer extends PrimitivePointer<Character> {

	private char value;

	public CharPointer() {
	}

	public CharPointer(final char value) {
		this.value = value;
	}

	public synchronized char getValue() {
		return this.value;
	}

	public synchronized void setValue(final char value) {
		this.set(value);
	}

	public synchronized char increment() {
		return this.add((char) 1);
	}

	public synchronized char decrement() {
		return this.sub((char) 1);
	}

	public synchronized char add(final char other) {
		return this.set(i -> (char) (i + other)).get();
	}

	public synchronized char mul(final char other) {
		return this.set(i -> (char) (i * other)).get();
	}

	public synchronized char sub(final char other) {
		return this.set(i -> (char) (i - other)).get();
	}

	public synchronized char div(final char other) {
		return this.set(i -> (char) (i / other)).get();
	}

	public synchronized char mod(final char other) {
		return this.set(i -> (char) (i % other)).get();
	}

	@Override
	public ObjectPointer<Character> toObjectPointer() {
		return new ObjectPointer<>(this.value);
	}

	@Override
	public synchronized Character get() {
		return this.value;
	}

	@Override
	public synchronized CharPointer set(final Character value) {
		Objects.requireNonNull(value);
		this.value = value;
		this.notifyAll();
		return this;
	}

}
