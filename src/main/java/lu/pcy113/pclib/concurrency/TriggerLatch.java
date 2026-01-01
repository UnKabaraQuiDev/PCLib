package lu.pcy113.pclib.concurrency;

import java.util.function.Predicate;

import lu.pcy113.pclib.pointer.prim.IntPointer;

public class TriggerLatch implements GenericTriggerLatch<Object> {

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
				onRelease.run();
			}

			return 0;
		}

	}

	private final Runnable onRelease;
	private final InternalIntPointer internalSize;

	public TriggerLatch(int value, Runnable onRelease) {
		this.onRelease = onRelease;
		this.internalSize = new InternalIntPointer(value);
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
