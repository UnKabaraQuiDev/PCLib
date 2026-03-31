package lu.kbra.pclib.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lu.kbra.pclib.pointer.prim.IntPointer;

public class ListTriggerLatch<E> implements Iterable<E>, GenericTriggerLatch<E> {

	protected class InternalIntPointer extends IntPointer {

		public InternalIntPointer(final int value) {
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
				ListTriggerLatch.this.onRelease.accept(ListTriggerLatch.this.list);
			}
			if (last && !ListTriggerLatch.this.latches.isEmpty()) {
				ListTriggerLatch.this.latches.forEach(latch -> latch.trigger(null));
			}

			return 0;
		}

	}

	private final List<E> list;
	private final Consumer<List<E>> onRelease;
	private final List<GenericTriggerLatch<?>> latches = new ArrayList<>();
	private final InternalIntPointer internalSize;

	public ListTriggerLatch(final int wantedSize, final Consumer<List<E>> onRelease) {
		this.onRelease = onRelease;
		this.list = new ArrayList<>();
		this.internalSize = new InternalIntPointer(wantedSize);
	}

	public ListTriggerLatch(final int wantedSize, final Consumer<List<E>> onRelease, final List<E> givenList) {
		this.onRelease = onRelease;
		this.list = givenList;
		if (givenList.size() >= wantedSize) {
			throw new IllegalArgumentException("Given list is already full (" + givenList.size() + "/" + wantedSize + ")");
		}
		this.internalSize = new InternalIntPointer(wantedSize - givenList.size());
	}

	public ListTriggerLatch<E> latch(final GenericTriggerLatch<?> latch) {
		this.latches.add(latch);
		return this;
	}

	@Override
	public Iterator<E> iterator() {
		return this.list.iterator();
	}

	public int size() {
		return this.list.size();
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public boolean contains(final Object o) {
		return this.list.contains(o);
	}

	@Override
	public void trigger(final E value) {
		this.add(value);
	}

	public boolean add(final E e) {
		final boolean val;
		synchronized (this) {
			val = this.list.add(e);
		}

		this.internalSize.decrement();

		return val;
	}

	public boolean addAll(final Collection<? extends E> c) {
		final boolean val;
		synchronized (this) {
			val = this.list.addAll(c);
		}

		this.internalSize.sub(c.size());

		return val;
	}

	public int getValue() {
		return this.internalSize.getValue();
	}

	public boolean waitForChange() {
		return this.internalSize.waitForChange();
	}

	public boolean waitForChange(final long timeout) {
		return this.internalSize.waitForChange(timeout);
	}

	public boolean waitForChange(final Predicate<Integer> condition) {
		return this.internalSize.waitForChange(condition);
	}

	public boolean waitForChange(final long timeout, final Predicate<Integer> condition) {
		return this.internalSize.waitForChange(timeout, condition);
	}

	public boolean waitForSet() {
		return this.internalSize.waitForSet();
	}

	public boolean waitForSet(final long timeout) {
		return this.internalSize.waitForSet(timeout);
	}

	public boolean waitForSet(final Predicate<Integer> condition) {
		return this.internalSize.waitForSet(condition);
	}

	public boolean waitForSet(final long timeout, final Predicate<Integer> condition) {
		return this.internalSize.waitForSet(timeout, condition);
	}

	public boolean join() {
		return this.internalSize.waitForSet(v -> v <= 0);
	}

	@Override
	public String toString() {
		return "ListTriggerLatch [list=" + this.list + ", onRelease=" + this.onRelease + ", internalSize=" + this.internalSize + "]";
	}

}
