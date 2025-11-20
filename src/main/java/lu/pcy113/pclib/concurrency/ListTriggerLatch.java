package lu.pcy113.pclib.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lu.pcy113.pclib.pointer.prim.IntPointer;

public class ListTriggerLatch<E> implements Iterable<E> {

	protected class InternalIntPointer extends IntPointer {

		public InternalIntPointer(int value) {
			super(value);
		}

		@Override
		public synchronized int decrement() {
			if (super.getValue() < 0) {
				throw new IllegalStateException("Cannot decrement into negatives.");
			}

			final boolean last = super.getValue() == 1;

			super.decrement();

			if (last) {
				onRelease.accept(list);
			}

			return 0;
		}

	}

	private final List<E> list;
	private final Consumer<List<E>> onRelease;
	private final InternalIntPointer internalSize;

	public ListTriggerLatch(int wantedSize, Consumer<List<E>> onRelease) {
		this.onRelease = onRelease;
		this.list = new ArrayList<>();
		this.internalSize = new InternalIntPointer(wantedSize);
	}

	public ListTriggerLatch(int wantedSize, Consumer<List<E>> onRelease, List<E> givenList) {
		this.onRelease = onRelease;
		this.list = givenList;
		if (givenList.size() >= wantedSize) {
			throw new IllegalArgumentException(
					"Given list is already full (" + givenList.size() + "/" + wantedSize + ")");
		}
		this.internalSize = new InternalIntPointer(wantedSize - givenList.size());
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	public int size() {
		return this.list.size();
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public boolean contains(Object o) {
		return this.list.contains(o);
	}

	public boolean add(E e) {
		final boolean val;
		synchronized (this) {
			val = list.add(e);
		}

		internalSize.decrement();

		return val;
	}

	public boolean addAll(Collection<? extends E> c) {
		final boolean val;
		synchronized (this) {
			val = list.addAll(c);
		}

		internalSize.sub(c.size());

		return val;
	}

	public int getValue() {
		return this.internalSize.getValue();
	}

	public boolean waitForChange() {
		return this.internalSize.waitForChange();
	}

	public boolean waitForChange(long timeout) {
		return this.internalSize.waitForChange(timeout);
	}

	public boolean waitForChange(Predicate<Integer> condition) {
		return this.internalSize.waitForChange(condition);
	}

	public boolean waitForChange(long timeout, Predicate<Integer> condition) {
		return this.internalSize.waitForChange(timeout, condition);
	}

	public boolean waitForSet() {
		return this.internalSize.waitForSet();
	}

	public boolean waitForSet(long timeout) {
		return this.internalSize.waitForSet(timeout);
	}

	public boolean waitForSet(Predicate<Integer> condition) {
		return this.internalSize.waitForSet(condition);
	}

	public boolean waitForSet(long timeout, Predicate<Integer> condition) {
		return this.internalSize.waitForSet(timeout, condition);
	}

	public boolean join() {
		return this.internalSize.waitForSet((v) -> v <= 0);
	}

	@Override
	public String toString() {
		return "ListTriggerLatch [list=" + this.list + ", onRelease=" + this.onRelease + ", internalSize="
				+ this.internalSize + "]";
	}

}
