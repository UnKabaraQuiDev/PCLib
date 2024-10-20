package lu.pcy113.pclib.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

import lu.pcy113.pclib.impl.ExceptionConsumer;
import lu.pcy113.pclib.impl.ExceptionFunction;

public class NextTask<I, O> {

	@SuppressWarnings("unused")
	private static class NextTaskStatus {

		private int state = IDLE;
		private boolean exceptionOccurred = false;

		public boolean isDone() {
			return state == DONE;
		}

		public boolean isError() {
			return state == ERROR;
		}

		public boolean isRunning() {
			return state == RUNNING;
		}

		public boolean isIdle() {
			return state == IDLE;
		}

		public boolean hasEnded() {
			return isDone() || isError();
		}

		public boolean exceptionOccurred() {
			return exceptionOccurred;
		}

	}

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	private static final int IDLE = 0;
	private static final int RUNNING = 1;
	private static final int DONE = 2;
	private static final int ERROR = 3;

	private final NextTaskStatus sharedState;

	private Function<Exception, O> catcher;
	private ExceptionFunction<I, O> task;

	public NextTask(ExceptionFunction<I, O> task) {
		this(task, new NextTaskStatus());
	}

	private NextTask(ExceptionFunction<I, O> task, NextTaskStatus sharedState) {
		this.task = task;
		this.sharedState = sharedState;
	}

	public <N> NextTask<I, N> thenCompose(ExceptionFunction<O, NextTask<I, N>> nextTaskFunction) {
		return new NextTask<>(input -> {
			if (sharedState.exceptionOccurred) {
				return null;
			}
			O result = this.run_(input);
			if (sharedState.exceptionOccurred) {
				return null;
			}
			NextTask<I, N> nextTask = nextTaskFunction.apply(result);
			return nextTask.run(input);
		}, sharedState);
	}

	public <N> NextTask<I, N> thenApply(ExceptionFunction<O, N> nextFunction) {
		return new NextTask<>(input -> {
			if (sharedState.exceptionOccurred) {
				return null;
			}
			O result = this.run_(input);
			if (sharedState.exceptionOccurred) {
				return null;
			}
			return nextFunction.apply(result);
		}, sharedState);
	}

	public NextTask<I, Void> thenConsume(ExceptionConsumer<O> nextRunnable) {
		return new NextTask<>(input -> {
			if (sharedState.exceptionOccurred) {
				return null;
			}
			O result = this.run_(input);
			if (!sharedState.exceptionOccurred) {
				nextRunnable.accept(result);
			}
			return null;
		}, sharedState);
	}

	public NextTask<I, O> catch_(Function<Exception, O> e) {
		this.catcher = e;
		return this;
	}

	private O run_(I input) {
		try {
			sharedState.state = RUNNING;
			O result = task.apply(input);
			sharedState.state = DONE;
			return result;
		} catch (Exception e) {
			sharedState.state = ERROR;
			sharedState.exceptionOccurred = true;
			if (catcher != null) {
				return catcher.apply(e);
			}
			return null;
		}
	}

	public O run(I input) {
		if (!sharedState.isIdle()) {
			throw new IllegalStateException("Already running or done");
		}

		return run_(input);
	}

	public O run() {
		return run(null);
	}

	public NextTaskStatus runAsync(I input) {
		if (!sharedState.isIdle()) {
			throw new IllegalStateException("Already running or done");
		}

		EXECUTOR.submit(() -> run(input));
		return sharedState;
	}

	public NextTaskStatus runAsync() {
		return runAsync(null);
	}

	public NextTaskStatus getTaskState() {
		return sharedState;
	}

	public static <I, O> NextTask<I, O> withArg(ExceptionFunction<I, O> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, O> create(ExceptionFunction<Void, O> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, O> create(Supplier<O> task) {
		return new NextTask<>((i) -> task.get());
	}

}
