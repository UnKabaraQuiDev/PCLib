package lu.pcy113.pclib.pointer;

public class ObjectPointer<T> implements JavaPointer {

	private T value;

	public ObjectPointer() {
	}

	public ObjectPointer(T value) {
		this.value = value;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public boolean isSet() {
		return this.value != null;
	}

}
