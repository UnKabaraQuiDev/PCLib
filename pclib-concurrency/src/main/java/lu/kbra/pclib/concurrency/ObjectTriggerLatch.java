package lu.kbra.pclib.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lu.kbra.pclib.impl.ThrowingConsumer;
import lu.kbra.pclib.pointer.prim.IntPointer;

public class ObjectTriggerLatch<T> implements GenericTriggerLatch<Object> {

	protected class InternalIntPointer extends IntPointer {

		public InternalIntPointer(final int value) {
			super(value);
		}

		@Override
		public synchronized int decrement() {
			if (super.getValue() <= 0) {
				return this.getValue();
//				throw new IllegalStateException("Cannot decrement into negatives.");
			}

			final boolean last = super.getValue() == 1;

			super.decrement();

			if (last && ObjectTriggerLatch.this.onReleases != null && !ObjectTriggerLatch.this.onReleases.isEmpty()) {
				ObjectTriggerLatch.this.onReleases.forEach(c -> c.accept(ObjectTriggerLatch.this.object));
			}
			if (last && !ObjectTriggerLatch.this.latches.isEmpty()) {
				ObjectTriggerLatch.this.latches.forEach(latch -> latch.trigger(ObjectTriggerLatch.this.object));
			}

			return 0;
		}

	}

	private final T object;
	private final List<Consumer<? super T>> onReleases = new ArrayList<>();
	private final List<GenericTriggerLatch<? super T>> latches = new ArrayList<>();
	private final InternalIntPointer internalSize;

	public ObjectTriggerLatch(final int count, final T value) {
		this.object = value;
		this.internalSize = new InternalIntPointer(count);
	}

	public ObjectTriggerLatch(final int count, final T value, final Consumer<T> onRelease) {
		this.onReleases.add(onRelease);
		this.object = value;
		this.internalSize = new InternalIntPointer(count);
	}

	public ObjectTriggerLatch<T> cancel() {
		this.onReleases.clear();
		this.latches.clear();
		this.internalSize.set(Integer.MIN_VALUE);
		return this;
	}

	public void countDown() {
		this.internalSize.decrement();
	}

	public T getObject() {
		return this.object;
	}

	public int getValue() {
		return this.internalSize.getValue();
	}

	public boolean join() {
		return this.internalSize.waitForSet(v -> v <= 0);
	}

	public ObjectTriggerLatch<T> latch(final GenericTriggerLatch<? super T> latch) {
		synchronized (this.internalSize) {
			if (this.internalSize.get() == 0) {
				latch.trigger(this.object);
			} else {
				this.latches.add(latch);
			}
		}
		return this;
	}

	public ObjectTriggerLatch<T> then(final Consumer<? super T> onRelease) {
		synchronized (this.internalSize) {
			if (this.internalSize.get() == 0) {
				onRelease.accept(this.object);
			} else {
				this.onReleases.add(onRelease);
			}
		}
		return this;
	}

	public <R extends Throwable> ObjectTriggerLatch<T> then(final ThrowingConsumer<? super T, R> onRelease) {
		synchronized (this.internalSize) {
			if (this.internalSize.get() == 0) {
				onRelease.asRuntime().accept(this.object);
			} else {
				this.onReleases.add(onRelease.asRuntime());
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return "ObjectTriggerLatch@" + System.identityHashCode(this) + " [object=" + this.object + ", onReleases=" + this.onReleases
				+ ", latches=" + this.latches + ", internalSize=" + this.internalSize + "]";
	}

	@Override
	public void trigger(final Object value) {
		this.countDown();
	}

	public boolean waitForChange() {
		return this.internalSize.waitForChange();
	}

	public boolean waitForChange(final long timeout) {
		return this.internalSize.waitForChange(timeout);
	}

	public boolean waitForChange(final long timeout, final Predicate<Integer> condition) {
		return this.internalSize.waitForChange(timeout, condition);
	}

	public boolean waitForChange(final Predicate<Integer> condition) {
		return this.internalSize.waitForChange(condition);
	}

	public boolean waitForSet() {
		return this.internalSize.waitForSet();
	}

	public boolean waitForSet(final long timeout) {
		return this.internalSize.waitForSet(timeout);
	}

	public boolean waitForSet(final long timeout, final Predicate<Integer> condition) {
		return this.internalSize.waitForSet(timeout, condition);
	}

	public boolean waitForSet(final Predicate<Integer> condition) {
		return this.internalSize.waitForSet(condition);
	}

}
