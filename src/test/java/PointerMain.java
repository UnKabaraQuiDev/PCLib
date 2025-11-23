import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.builder.ThreadBuilder;
import lu.pcy113.pclib.pointer.prim.IntPointer;
import lu.pcy113.pclib.pointer.prim.LongPointer;

public class PointerMain {

	@Test
	public void waitForSet() throws InterruptedException {
		final IntPointer OBJ = new IntPointer(0);

		final LongPointer reachedEnd = new LongPointer(0);
		final Thread thread1 = ThreadBuilder.create((Runnable) () -> {
			OBJ.waitForSet();
			System.out.println("Thread 1: " + OBJ.get());
			reachedEnd.set(System.nanoTime());
		}).start();

		Thread.sleep(250);

		OBJ.set(42);
		final long changeTime = System.nanoTime();

		Thread.sleep(250);

		assert !thread1.isAlive() : "Thread 1 should have finished.";
		assert reachedEnd.get() > 0 : "Thread 1 should have reached the end.";

		System.out.println("Changed in: " + ((double) (reachedEnd.get() - changeTime) / 1_000_000) + "ms");
	}

	@Test
	public void waitForChange() throws InterruptedException {
		final IntPointer OBJ = new IntPointer(12);

		final LongPointer reachedEnd2 = new LongPointer(0);
		final Thread thread2 = ThreadBuilder.create((Runnable) () -> {
			OBJ.waitForSet();
			System.out.println("Thread 2: " + OBJ.get());
			reachedEnd2.set(System.nanoTime());
		}).start();

		final LongPointer reachedEnd3 = new LongPointer(0);
		final Thread thread3 = ThreadBuilder.create((Runnable) () -> {
			OBJ.waitForChange();
			System.out.println("Thread 3: " + OBJ.get());
			reachedEnd3.set(System.nanoTime());
		}).start();

		Thread.sleep(250);

		OBJ.set(12); // shouldn't trigger change but set
		final long changeTime = System.nanoTime();

		Thread.sleep(250);

		assert !thread2.isAlive() : "Thread 2 should have finished.";
		assert reachedEnd2.get() > 0 : "Thread 2 should have reached the end.";

		assert thread3.isAlive() : "Thread 3 shouldn't have finished.";
		assert reachedEnd3.get() == 0 : "Thread 3 shouldn't have reached the end.";
		thread3.interrupt();

		System.out.println("Changed in: " + ((double) (reachedEnd2.get() - changeTime) / 1_000_000) + "ms");
	}

	@Test
	public void waitForSetTimeout() throws InterruptedException {
		final IntPointer OBJ = new IntPointer(12);

		final LongPointer reachedEnd4 = new LongPointer(0);
		final IntPointer reachedValue4 = new IntPointer(0);
		final Thread thread4 = ThreadBuilder.create((Runnable) () -> {
			OBJ.waitForSet(100);
			reachedValue4.set(OBJ.get());
			System.out.println("Thread 4: " + reachedValue4.get());
			reachedEnd4.set(System.nanoTime());
		}).start();

		final LongPointer reachedEnd5 = new LongPointer(0);
		final IntPointer reachedValue5 = new IntPointer(0);
		final Thread thread5 = ThreadBuilder.create((Runnable) () -> {
			OBJ.waitForSet(300);
			reachedValue5.set(OBJ.get());
			System.out.println("Thread 5: " + reachedValue5.get());
			reachedEnd5.set(System.nanoTime());
		}).start();

		Thread.sleep(250);

		OBJ.set(14);
		final long changeTime = System.nanoTime();

		Thread.sleep(250);

		assert !thread4.isAlive() : "Thread 4 should have finished.";
		assert reachedEnd4.get() > 0 : "Thread 4 should have reached the end.";
		assert reachedValue4.get() == 12 : "Thread 4 should have kept original value.";

		assert !thread5.isAlive() : "Thread 5 should have finished.";
		assert reachedEnd5.get() > 0 : "Thread 5 should have reached the end.";
		assert reachedValue5.get() == 14 : "Thread 5 should have changed value to 14.";

		System.out.println("Unchanged in: " + ((double) (reachedEnd4.get() - changeTime) / 1_000_000) + "ms");
		System.out.println("Changed in: " + ((double) (reachedEnd5.get() - changeTime) / 1_000_000) + "ms");
	}

}
