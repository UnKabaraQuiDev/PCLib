package lu.pcy113.pclib.concurrency;

import lu.pcy113.pclib.pointer.prim.IntPointer;

public class TriggerLatch extends IntPointer {

	private final Runnable onRelease;

	public TriggerLatch(int value, Runnable onRelease) {
		super(value);
		this.onRelease = onRelease;
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
