package lu.kbra.pclib.pointer;

import java.util.function.Function;

public class ObjectPointer<T> extends JavaPointer<T> {

	private T value;

	public ObjectPointer() {
		value = null;
	}

	public ObjectPointer(T value) {
		this.value = value;
	}

	public synchronized <N> ObjectPointer<N> map(Function<T, N> func) {
		return new ObjectPointer<>(isSet() ? func.apply(value) : null);
	}

	@Override
	public synchronized boolean isSet() {
		return this.value != null;
	}

	@Override
	public synchronized String toString() {
		return getClass().getName() + "[" + value + "]";
	}

	@Override
	public synchronized T get() {
		return value;
	}

	@Override
	public synchronized ObjectPointer<T> set(T value) {
		this.value = value;
		this.notifyAll();
		return this;
	}

	@Override
	public synchronized ObjectPointer<T> set(Function<T, T> func) {
		return (ObjectPointer<T>) super.set(func);
	}

}
