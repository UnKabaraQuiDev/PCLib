package lu.kbra.pclib.pointer;

import java.util.function.Function;

public class ObjectPointer<T> extends JavaPointer<T> {

	private T value;

	public ObjectPointer() {
		this.value = null;
	}

	public ObjectPointer(final T value) {
		this.value = value;
	}

	public synchronized <N> ObjectPointer<N> map(final Function<T, N> func) {
		return new ObjectPointer<>(this.isSet() ? func.apply(this.value) : null);
	}

	@Override
	public synchronized boolean isSet() {
		return this.value != null;
	}

	@Override
	public synchronized String toString() {
		return this.getClass().getName() + "[" + this.value + "]";
	}

	@Override
	public synchronized T get() {
		return this.value;
	}

	@Override
	public synchronized ObjectPointer<T> set(final T value) {
		this.value = value;
		this.notifyAll();
		return this;
	}

	@Override
	public synchronized ObjectPointer<T> set(final Function<T, T> func) {
		return (ObjectPointer<T>) super.set(func);
	}

}
