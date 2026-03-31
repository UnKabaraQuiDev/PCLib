package lu.kbra.pclib.pointer;

public class StringPointer extends ObjectPointer<String> {

	public StringPointer() {
	}

	public StringPointer(final String value) {
		super(value);
	}

	public synchronized StringPointer append(final Object obj) {
		return this.set(this.get() + obj != null ? obj.toString() : null);
	}

	public synchronized StringPointer append(final ObjectPointer<?> obj) {
		return this.set(this.get() + obj != null ? obj.get() != null ? obj.get().toString() : obj.toString() : null);
	}

	@Override
	public synchronized StringPointer set(final String value) {
		super.set(value);
		return this;
	}

}
