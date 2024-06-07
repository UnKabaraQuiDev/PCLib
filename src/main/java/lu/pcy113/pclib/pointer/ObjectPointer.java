package lu.pcy113.pclib.pointer;

public class ObjectPointer<T> implements JavaPointer {

	private T value;

	public ObjectPointer() {
		value = null;
	}

	public ObjectPointer(T value) {
		this.value = value;
	}

	public synchronized T getValue() {
		return this.value;
	}

	public synchronized ObjectPointer<T> setValue(T value) {
		this.value = value;
		return this;
	}

	@Override
	public synchronized boolean isSet() {
		return this.value != null;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + value + "]";
	}

}
