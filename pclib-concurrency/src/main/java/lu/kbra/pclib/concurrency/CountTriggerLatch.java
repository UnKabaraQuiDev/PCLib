package lu.kbra.pclib.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import lu.kbra.pclib.pointer.prim.IntPointer;

public class CountTriggerLatch implements GenericTriggerLatch<Object> {

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

			if (last && CountTriggerLatch.this.onRelease != null) {
				CountTriggerLatch.this.onRelease.run();
			}
			if (last && !CountTriggerLatch.this.latches.isEmpty()) {
				CountTriggerLatch.this.latches.forEach(latch -> latch.trigger(null));
			}

			return 0;
		}

	}

	private final Runnable onRelease;
	private final List<GenericTriggerLatch<?>> latches = new ArrayList<>();
	private final InternalIntPointer internalSize;

	public CountTriggerLatch(final int value, final Runnable onRelease) {
		this.onRelease = onRelease;
		this.internalSize = new InternalIntPointer(value);
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
