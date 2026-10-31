package lu.kbra.pclib.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TriggerLatchTest {

	@Test
	void countLatchReleasesAtZero() {
		final AtomicInteger releases = new AtomicInteger();
		final CountTriggerLatch latch = new CountTriggerLatch(2, releases::incrementAndGet);

		latch.countDown();
		Assertions.assertEquals(1, latch.getValue());
		Assertions.assertEquals(0, releases.get());

		latch.trigger(null);
		Assertions.assertEquals(0, latch.getValue());
		Assertions.assertEquals(1, releases.get());
		Assertions.assertTrue(latch.join());
		Assertions.assertTrue(latch.waitForSet(100, value -> value == 0));
	}

	@Test
	void deferredLatchPassesTheLastValueToCallbacksAndLinkedLatches() {
		final AtomicReference<String> released = new AtomicReference<>();
		final AtomicReference<String> forwarded = new AtomicReference<>();
		final DeferredTriggerLatch<String> latch = new DeferredTriggerLatch<>(2);

		latch.then(released::set).latch(forwarded::set);
		latch.countDown();
		Assertions.assertEquals(1, latch.getValue());
		Assertions.assertEquals(null, released.get());

		latch.trigger("complete");
		Assertions.assertEquals(0, latch.getValue());
		Assertions.assertTrue(latch.join());
		Assertions.assertEquals("complete", latch.getObject());
		Assertions.assertEquals("complete", released.get());
		Assertions.assertEquals("complete", forwarded.get());
	}

	@Test
	void cancelledDeferredLatchDoesNotNotifyListeners() {
		final AtomicInteger releases = new AtomicInteger();
		final DeferredTriggerLatch<String> latch = new DeferredTriggerLatch<>(1, "value", value -> releases.incrementAndGet());
		latch.latch(value -> releases.incrementAndGet()).cancel();
		latch.countDown();

		Assertions.assertEquals(0, latch.getValue());
		Assertions.assertEquals(0, releases.get());
	}

	@Test
	void objectLatchNotifiesOnceAndInvokesLateListeners() {
		final AtomicInteger releases = new AtomicInteger();
		final AtomicReference<String> forwarded = new AtomicReference<>();
		final ObjectTriggerLatch<String> latch = new ObjectTriggerLatch<>(2, "payload", value -> releases.incrementAndGet());

		latch.then((Consumer<String>) value -> releases.incrementAndGet()).latch(forwarded::set);
		latch.countDown();
		Assertions.assertEquals(1, latch.getValue());
		Assertions.assertEquals(0, releases.get());

		latch.countDown();
		Assertions.assertEquals(0, latch.getValue());
		Assertions.assertEquals(2, releases.get());
		Assertions.assertEquals("payload", forwarded.get());
		Assertions.assertTrue(latch.join());

		latch.then((Consumer<String>) value -> releases.incrementAndGet());
		Assertions.assertEquals(3, releases.get());
		latch.countDown();
		Assertions.assertEquals(0, latch.getValue());
		Assertions.assertEquals(3, releases.get());
	}

	@Test
	void listLatchCollectsElementsAndNotifiesWhenFull() {
		final AtomicReference<List<Integer>> result = new AtomicReference<>();
		final AtomicInteger linked = new AtomicInteger();
		final ListTriggerLatch<Integer> latch = new ListTriggerLatch<>(3, values -> result.set(new ArrayList<>(values)));
		latch.latch(value -> linked.incrementAndGet());

		Assertions.assertTrue(latch.add(1));
		Assertions.assertEquals(2, latch.getValue());
		Assertions.assertTrue(latch.add(2));
		Assertions.assertTrue(latch.add(3));

		Assertions.assertEquals(Arrays.asList(1, 2, 3), result.get());
		Assertions.assertEquals(3, latch.size());
		Assertions.assertTrue(latch.contains(2));
		Assertions.assertFalse(latch.isEmpty());
		Assertions.assertEquals(0, latch.getValue());
		Assertions.assertEquals(1, linked.get());
		Assertions.assertTrue(latch.join());
	}

	@Test
	void listLatchRejectsAnAlreadyFullInitialList() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new ListTriggerLatch<>(2, values -> {
		}, Arrays.asList(1, 2)));
	}

}
