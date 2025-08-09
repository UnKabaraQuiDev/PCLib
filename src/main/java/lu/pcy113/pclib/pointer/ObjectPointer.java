package lu.pcy113.pclib.pointer;

public class ObjectPointer<T> extends JavaPointer<T> {

	private T value;

	public ObjectPointer() {
		value = null;
	}

	public ObjectPointer(T value) {
		this.value = value;
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

}
