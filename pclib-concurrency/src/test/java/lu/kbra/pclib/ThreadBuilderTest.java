package lu.kbra.pclib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class ThreadBuilderTest {

	@Test
	void builderConfiguresAThreadWithoutStartingIt() {
		final Thread thread = ThreadBuilder.create((Runnable) () -> {
		}).name("pclib-test-thread").daemon(true).priority(Thread.NORM_PRIORITY).build();

		assertEquals("pclib-test-thread", thread.getName());
		assertTrue(thread.isDaemon());
		assertEquals(Thread.NORM_PRIORITY, thread.getPriority());
		assertFalse(thread.isAlive());
	}

	@Test
	void startedThreadExecutesTask() throws InterruptedException {
		final CountDownLatch completed = new CountDownLatch(1);
		final Thread thread = ThreadBuilder.create((Runnable) completed::countDown).name("pclib-worker").start();

		assertTrue(completed.await(5, TimeUnit.SECONDS));
		thread.join(5000);
		assertFalse(thread.isAlive());
	}

}
