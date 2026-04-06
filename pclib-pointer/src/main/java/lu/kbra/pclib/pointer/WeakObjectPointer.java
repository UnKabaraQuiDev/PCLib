package lu.kbra.pclib.pointer;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public class WeakObjectPointer<T> extends JavaPointer<T> {

	private WeakReference<T> value;

	public WeakObjectPointer() {
		this.value = null;
	}

	public WeakObjectPointer(final T value) {
		this.value = new WeakReference<>(value);
	}

	public synchronized <N> ObjectPointer<N> map(final Function<T, N> func) {
		return new ObjectPointer<>(this.isSet() ? func.apply(this.value.get()) : null);
	}

	public synchronized <N> WeakObjectPointer<N> weakMap(final Function<T, N> func) {
		return new WeakObjectPointer<>(this.isSet() ? func.apply(this.value.get()) : null);
	}

	@Override
	public synchronized boolean isSet() {
		return this.value != null && value.get() != null;
	}

	@Override
	public synchronized String toString() {
		return this.getClass().getName() + "[" + this.value + "]";
	}

	@Override
	public synchronized T get() {
		return this.value == null ? null : value.get();
	}

	@Override
	public synchronized WeakObjectPointer<T> set(final T value) {
		this.value = new WeakReference<>(value);
		this.notifyAll();
		return this;
	}

	@Override
	public synchronized ObjectPointer<T> set(final Function<T, T> func) {
		return (ObjectPointer<T>) super.set(func);
	}

}
