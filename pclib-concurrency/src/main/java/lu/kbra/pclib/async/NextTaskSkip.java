package lu.kbra.pclib.async;

public class NextTaskSkip extends RuntimeException {

	private static final long serialVersionUID = -7814574372911077158L;
	private int count = 1;
	private Object obj;
	private NextTask next;

	public NextTaskSkip(final Object obj) {
		this.obj = obj;
	}

	public NextTaskSkip(final NextTask next) {
		this.next = next;
	}

	public NextTaskSkip(final int count, final Object obj) {
		this.count = count;
		this.obj = obj;
	}

	public NextTaskSkip(final int count, final NextTask next) {
		this.count = count;
		this.next = next;
	}

	public NextTaskSkip(final int count, final Object obj, final NextTask next) {
		this.count = count;
		this.obj = obj;
		this.next = next;
	}

	public Object getObj() {
		return this.obj;
	}

	public int getCount() {
		return this.count;
	}

	public NextTask getNext() {
		return this.next;
	}

	@Override
	public String toString() {
		return "NextTaskSkip [count=" + this.count + ", obj=" + this.obj + ", next=" + this.next + "]";
	}

}
