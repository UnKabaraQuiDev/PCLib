package lu.pcy113.pclib.pointer;

public class StringPointer extends ObjectPointer<String> {

	public StringPointer() {
	}

	public StringPointer(String value) {
		super(value);
	}

	public synchronized StringPointer append(Object obj) {
		return set(get() + obj != null ? obj.toString() : null);
	}

	public synchronized StringPointer append(ObjectPointer<?> obj) {
		return set(get() + obj != null ? (obj.get() != null ? obj.get().toString() : obj.toString()) : null);
	}

	@Override
	public synchronized StringPointer set(String value) {
		super.set(value);
		return this;
	}

}
