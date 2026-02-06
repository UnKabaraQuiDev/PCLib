package lu.kbra.pclib.async;

public class NextTaskSkip extends RuntimeException {

	private int count = 1;
	private Object obj;
	private NextTask next;

	public NextTaskSkip(Object obj) {
		this.obj = obj;
	}

	public NextTaskSkip(NextTask next) {
		this.next = next;
	}

	public NextTaskSkip(int count, Object obj) {
		this.count = count;
		this.obj = obj;
	}

	public NextTaskSkip(int count, NextTask next) {
		this.count = count;
		this.next = next;
	}

	public NextTaskSkip(int count, Object obj, NextTask next) {
		this.count = count;
		this.obj = obj;
		this.next = next;
	}

	public Object getObj() {
		return obj;
	}

	public int getCount() {
		return count;
	}

	public NextTask getNext() {
		return next;
	}

	@Override
	public String toString() {
		return "NextTaskSkip [count=" + count + ", obj=" + obj + ", next=" + next + "]";
	}

}
