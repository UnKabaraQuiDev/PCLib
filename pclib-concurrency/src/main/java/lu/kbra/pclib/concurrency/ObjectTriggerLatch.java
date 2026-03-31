package lu.kbra.pclib.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lu.kbra.pclib.pointer.prim.IntPointer;

public class ObjectTriggerLatch<T> implements GenericTriggerLatch<Object> {

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

			if (last && ObjectTriggerLatch.this.onRelease != null) {
				ObjectTriggerLatch.this.onRelease.accept(ObjectTriggerLatch.this.object);
			}
			if (last && !ObjectTriggerLatch.this.latches.isEmpty()) {
				ObjectTriggerLatch.this.latches.forEach(latch -> latch.trigger(ObjectTriggerLatch.this.object));
			}

			return 0;
		}

	}

	private final T object;
	private Consumer<T> onRelease;
	private final List<GenericTriggerLatch<? super T>> latches = new ArrayList<>();
	private final InternalIntPointer internalSize;

	public ObjectTriggerLatch(final int count, final T value) {
		this.object = value;
		this.internalSize = new InternalIntPointer(count);
	}

	public ObjectTriggerLatch(final int count, final T value, final Consumer<T> onRelease) {
		this.onRelease = onRelease;
		this.object = value;
		this.internalSize = new InternalIntPointer(count);
	}

	public ObjectTriggerLatch<T> then(final Consumer<T> onRelease) {
		this.onRelease = onRelease;
		return this;
	}

	public ObjectTriggerLatch<T> latch(final GenericTriggerLatch<? super T> latch) {
		this.latches.add(latch);
		return this;
	}

	public ObjectTriggerLatch<T> thenOther(final Consumer<T> onRelease) {
		this.latches.add(new ObjectTriggerLatch<>(1, this.object, onRelease));
		return this;
	}

	public ObjectTriggerLatch<T> cancel() {
		this.onRelease = null;
		this.latches.clear();
		return this;
	}

	@Override
	public void trigger(final Object value) {
		this.countDown();
	}

	public void countDown() {
		this.internalSize.decrement();
	}

	public int getValue() {
		return this.internalSize.getValue();
	}

	public T getObject() {
		return this.object;
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
		return "TriggerLatch [onRelease=" + this.onRelease + ", internalSize=" + this.internalSize + "]";
	}

}
