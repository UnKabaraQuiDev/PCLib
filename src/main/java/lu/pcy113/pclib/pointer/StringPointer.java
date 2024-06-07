package lu.pcy113.pclib.pointer;

public class StringPointer extends ObjectPointer<String> {

	public StringPointer() {
	}

	public StringPointer(String value) {
		super(value);
	}

	public synchronized StringPointer append(Object obj) {
		return setValue(getValue() + obj != null ? obj.toString() : null);
	}

	public synchronized StringPointer append(ObjectPointer<?> obj) {
		return setValue(getValue() + obj != null ? (obj.getValue() != null ? obj.getValue().toString() : obj.toString()) : null);
	}

	@Override
	public StringPointer setValue(String value) {
		super.setValue(value);
		return this;
	}

}
