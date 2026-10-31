package lu.kbra.pclib;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ThreadBuilderTest {

	@Test
	void builderConfiguresAThreadWithoutStartingIt() {
		final Thread thread = ThreadBuilder.create((Runnable) () -> {
		}).name("pclib-test-thread").daemon(true).priority(Thread.NORM_PRIORITY).build();

		Assertions.assertEquals("pclib-test-thread", thread.getName());
		Assertions.assertTrue(thread.isDaemon());
		Assertions.assertEquals(Thread.NORM_PRIORITY, thread.getPriority());
		Assertions.assertFalse(thread.isAlive());
	}

	@Test
	void startedThreadExecutesTask() throws InterruptedException {
		final CountDownLatch completed = new CountDownLatch(1);
		final Thread thread = ThreadBuilder.create((Runnable) completed::countDown).name("pclib-worker").start();

		Assertions.assertTrue(completed.await(5, TimeUnit.SECONDS));
		thread.join(5000);
		Assertions.assertFalse(thread.isAlive());
	}

}
