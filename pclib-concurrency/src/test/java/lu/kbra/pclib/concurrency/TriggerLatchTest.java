package lu.kbra.pclib.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

class TriggerLatchTest {

	@Test
	void countLatchReleasesAtZero() {
		final AtomicInteger releases = new AtomicInteger();
		final CountTriggerLatch latch = new CountTriggerLatch(2, releases::incrementAndGet);

		latch.countDown();
		assertEquals(1, latch.getValue());
		assertEquals(0, releases.get());

		latch.trigger(null);
		assertEquals(0, latch.getValue());
		assertEquals(1, releases.get());
		assertTrue(latch.join());
		assertTrue(latch.waitForSet(100, value -> value == 0));
	}

	@Test
	void deferredLatchPassesTheLastValueToCallbacksAndLinkedLatches() {
		final AtomicReference<String> released = new AtomicReference<>();
		final AtomicReference<String> forwarded = new AtomicReference<>();
		final DeferredTriggerLatch<String> latch = new DeferredTriggerLatch<>(2);

		latch.then(released::set).latch(forwarded::set);
		latch.countDown();
		assertEquals(1, latch.getValue());
		assertEquals(null, released.get());

		latch.trigger("complete");
		assertEquals(0, latch.getValue());
		assertTrue(latch.join());
		assertEquals("complete", latch.getObject());
		assertEquals("complete", released.get());
		assertEquals("complete", forwarded.get());
	}

	@Test
	void cancelledDeferredLatchDoesNotNotifyListeners() {
		final AtomicInteger releases = new AtomicInteger();
		final DeferredTriggerLatch<String> latch = new DeferredTriggerLatch<>(1, "value", value -> releases.incrementAndGet());
		latch.latch(value -> releases.incrementAndGet()).cancel();
		latch.countDown();

		assertEquals(0, latch.getValue());
		assertEquals(0, releases.get());
	}

	@Test
	void objectLatchNotifiesOnceAndInvokesLateListeners() {
		final AtomicInteger releases = new AtomicInteger();
		final AtomicReference<String> forwarded = new AtomicReference<>();
		final ObjectTriggerLatch<String> latch = new ObjectTriggerLatch<>(2, "payload", value -> releases.incrementAndGet());

		latch.then((Consumer<String>) value -> releases.incrementAndGet()).latch(forwarded::set);
		latch.countDown();
		assertEquals(1, latch.getValue());
		assertEquals(0, releases.get());

		latch.countDown();
		assertEquals(0, latch.getValue());
		assertEquals(2, releases.get());
		assertEquals("payload", forwarded.get());
		assertTrue(latch.join());

		latch.then((Consumer<String>) value -> releases.incrementAndGet());
		assertEquals(3, releases.get());
		latch.countDown();
		assertEquals(0, latch.getValue());
		assertEquals(3, releases.get());
	}

	@Test
	void listLatchCollectsElementsAndNotifiesWhenFull() {
		final AtomicReference<List<Integer>> result = new AtomicReference<>();
		final AtomicInteger linked = new AtomicInteger();
		final ListTriggerLatch<Integer> latch = new ListTriggerLatch<>(3, values -> result.set(new ArrayList<>(values)));
		latch.latch(value -> linked.incrementAndGet());

		assertTrue(latch.add(1));
		assertEquals(2, latch.getValue());
		assertTrue(latch.add(2));
		assertTrue(latch.add(3));

		assertEquals(Arrays.asList(1, 2, 3), result.get());
		assertEquals(3, latch.size());
		assertTrue(latch.contains(2));
		assertFalse(latch.isEmpty());
		assertEquals(0, latch.getValue());
		assertEquals(1, linked.get());
		assertTrue(latch.join());
	}

	@Test
	void listLatchRejectsAnAlreadyFullInitialList() {
		assertThrows(IllegalArgumentException.class, () -> new ListTriggerLatch<>(2, values -> {
		}, Arrays.asList(1, 2)));
	}

}
