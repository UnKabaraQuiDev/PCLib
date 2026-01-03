package lu.pcy113.pclib.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lu.pcy113.pclib.pointer.prim.IntPointer;

public class ObjectTriggerLatch<T> implements GenericTriggerLatch<Object> {

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

			if (last && onRelease != null) {
				onRelease.accept(object);
			}
			if (last && !latches.isEmpty()) {
				latches.forEach(latch -> latch.trigger(object));
			}

			return 0;
		}

	}

	private final T object;
	private Consumer<T> onRelease;
	private List<GenericTriggerLatch<? super T>> latches = new ArrayList<>();
	private final InternalIntPointer internalSize;

	public ObjectTriggerLatch(int count, T value) {
		this.object = value;
		this.internalSize = new InternalIntPointer(count);
	}

	public ObjectTriggerLatch(int count, T value, Consumer<T> onRelease) {
		this.onRelease = onRelease;
		this.object = value;
		this.internalSize = new InternalIntPointer(count);
	}

	public ObjectTriggerLatch<T> then(Consumer<T> onRelease) {
		this.onRelease = onRelease;
		return this;
	}

	public ObjectTriggerLatch<T> latch(GenericTriggerLatch<? super T> latch) {
		latches.add(latch);
		return this;
	}

	public ObjectTriggerLatch<T> cancel() {
		this.onRelease = null;
		latches.clear();
		return this;
	}

	@Override
	public void trigger(Object value) {
		countDown();
	}

	public void countDown() {
		internalSize.decrement();
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
		return "TriggerLatch [onRelease=" + this.onRelease + ", internalSize=" + this.internalSize + "]";
	}

}
