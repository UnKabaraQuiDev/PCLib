package lu.kbra.pclib.async;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

class NextTaskTest {

	@Test
	void chainedTasksPassValuesThroughEachStage() {
		final NextTask<Integer, Integer, Integer> task = NextTask.withArg((Integer value) -> value + 1).thenApply(value -> value * 2);

		assertTrue(task.getTaskState().isIdle());
		assertEquals(Integer.valueOf(8), task.run(3));
		assertTrue(task.getTaskState().isDone());
	}

	@Test
	void chainCollectsEachIntermediateResult() {
		final NextTask<Integer, Integer, java.util.List<Integer>> task = NextTask.chain(value -> value + 2, value -> value * 3);

		assertEquals(Arrays.asList(1, 3, 9), task.run(1));
	}

	@Test
	void collectAndParallelReturnResultsInTaskOrder() {
		final NextTask<Void, Void, java.util.List<Integer>> collected = NextTask.collect(NextTask.create(() -> 2),
				NextTask.create(() -> 4));
		assertEquals(Arrays.asList(2, 4), collected.run());

		final NextTask<Integer, Integer, java.util.List<Integer>> parallel = NextTask.parallel(NextTask.withArg((Integer n) -> n + 1),
				NextTask.withArg((Integer n) -> n * 2));
		assertEquals(Arrays.asList(4, 6), parallel.run(3));
	}

	@Test
	void catcherReceivesCheckedFailureWithoutReflection() {
		final IOException failure = new IOException("failed");
		final AtomicReference<Throwable> received = new AtomicReference<>();
		final NextTask<Void, Void, Integer> task = NextTask.create((ThrowingSupplier<Integer, Throwable>) () -> {
			throw failure;
		});
		task.catch_(received::set);

		assertEquals(null, task.run());
		assertSame(failure, received.get());
		assertTrue(task.getTaskState().isError());
		assertFalse(task.getTaskState().isDone());
	}

}
