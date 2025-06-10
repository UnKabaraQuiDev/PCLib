package lu.pcy113.pclib.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.impl.ExceptionConsumer;
import lu.pcy113.pclib.impl.ExceptionFunction;
import lu.pcy113.pclib.impl.ExceptionRunnable;
import lu.pcy113.pclib.impl.ExceptionSupplier;

public class NextTask<I, O> {

	protected static class NextTaskStatus {

		protected int state = IDLE;

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

	}

	protected static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	protected static final int IDLE = 0;
	protected static final int RUNNING = 1;
	protected static final int DONE = 2;
	protected static final int ERROR = 3;

	protected final NextTask<?, ?> parent;
	protected final NextTaskStatus sharedState;

	protected ExceptionConsumer<Throwable> catcher;
	protected ExceptionFunction<I, O> task;

	public NextTask(ExceptionFunction<I, O> task) {
		this.task = task;
		this.parent = null;
		this.sharedState = new NextTaskStatus();
		this.catcher = e -> PCUtils.throw_(e);
	}

	protected NextTask(ExceptionFunction<I, O> task, NextTask<?, ?> parent) {
		this.task = task;
		this.parent = parent;
		this.sharedState = parent.sharedState;
	}

	public <N> NextTask<I, N> thenCompose(ExceptionFunction<O, NextTask<I, N>> nextTaskFunction) {
		return new NextTask<>(input -> {
			O result = this.run_(input);
			if (!sharedState.isError()) {
				NextTask<I, N> nextTask = nextTaskFunction.apply(result);
				return nextTask.run(input);
			}
			return null;
		}, this);
	}

	public <N> NextTask<I, N> thenCompose(NextTask<O, N> nextTaskFunction) {
		return new NextTask<>(input -> {
			O result = this.run_(input);
			if (!sharedState.isError()) {
				return nextTaskFunction.run(result);
			}
			return null;
		}, this);
	}

	public <N> NextTask<I, N> thenApply(ExceptionFunction<O, N> nextFunction) {
		return new NextTask<>(input -> {
			O result = this.run_(input);
			if (!sharedState.isError()) {
				return nextFunction.apply(result);
			}
			return null;
		}, this);
	}

	public NextTask<I, O> thenParallel(ExceptionConsumer<O> nextFunction) {
		return new NextTask<>(input -> {
			O result = this.run_(input);
			if (!sharedState.isError()) {
				nextFunction.accept(result);
				return result;
			}
			return null;
		}, this);
	}

	public NextTask<I, Void> thenConsume(ExceptionConsumer<O> nextRunnable) {
		return new NextTask<>(input -> {
			O result = this.run_(input);
			if (!sharedState.isError()) {
				nextRunnable.accept(result);
			}
			return null;
		}, this);
	}

	public NextTask<I, O> orElse(O n) {
		return thenApply(o -> o == null ? n : o);
	}

	public NextTask<I, O> orElseThrow(Throwable throw_) {
		return thenApply((ExceptionFunction<O, O>) o -> {
			if (o == null) {
				throw throw_;
			} else {
				return o;
			}
		});
	}

	public NextTask<I, O> orElse(ExceptionSupplier<O> n) {
		return thenApply(o -> o == null ? n.get() : o);
	}

	public NextTask<I, O> orElseThrow(ExceptionSupplier<Throwable> throw_) {
		return thenApply((ExceptionFunction<O, O>) o -> {
			if (o == null) {
				throw throw_.get();
			} else {
				return o;
			}
		});
	}

	public NextTask<I, O> orElse(NextTask<Void, O> n) {
		return thenApply(o -> o == null ? n.run() : o);
	}

	public NextTask<I, Optional<O>> toOptional() {
		return thenApply(o -> Optional.ofNullable(o));
	}

	public NextTask<I, O> catch_(ExceptionConsumer<Throwable> e) {
		/*
		 * if (catcher != null) { throw new
		 * IllegalStateException("A catcher was already registered for this NextTask.");
		 * }
		 */
		this.catcher = e;
		return this;
	}

	protected synchronized O run_(I input) throws Throwable {
		try {
			if (catcher == null) {
				// set default catcher for last child
				catcher = PCUtils::throw_;
			}
			sharedState.state = RUNNING;
			O result = task.apply(input);
			sharedState.state = DONE;
			return result;
		} catch (Throwable e) {
			sharedState.state = ERROR;
			propagateException(e);
			return null;
		}
	}

	protected void propagateException(Throwable e) throws Throwable {
		if (catcher != null) {
			catcher.accept(e);
		} else if (parent != null) {
			parent.propagateException(e);
		}
	}

	public synchronized O run(I input) {
		try {
			return runThrow(input);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized O run() {
		return run(null);
	}

	public synchronized O runThrow(I input) throws Throwable {
		if (!sharedState.isIdle()) {
			throw new IllegalStateException("Already running or done");
		}

		return run_(input);
	}

	public synchronized O runThrow() throws Throwable {
		return runThrow(null);
	}

	public synchronized NextTaskStatus runAsync(I input) {
		if (!sharedState.isIdle()) {
			throw new IllegalStateException("Already running or done");
		}

		EXECUTOR.submit(() -> run(input));
		return sharedState;
	}

	public synchronized NextTaskStatus runAsync() {
		return runAsync(null);
	}

	public NextTaskStatus getTaskState() {
		return sharedState;
	}

	public static <I> NextTask<I, Void> withArg(ExceptionConsumer<I> task) {
		return new NextTask<>((a) -> {
			task.accept(a);
			return null;
		});
	}

	public static <I, O> NextTask<I, O> withArg(ExceptionFunction<I, O> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, O> create(ExceptionFunction<Void, O> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, O> create(ExceptionSupplier<O> task) {
		return new NextTask<>((i) -> task.get());
	}

	public static NextTask<Void, Void> create(ExceptionRunnable task) {
		return new NextTask<>((i) -> {
			task.run();
			return null;
		});
	}

	public static <O> NextTask<Void, O> empty() {
		return new NextTask<Void, O>((Void v) -> null);
	}

	@SafeVarargs
	public static <I, O> NextTask<I, List<O>> parallel(NextTask<I, O>... tasks) {
		return new NextTask<>((arg) -> {
			List<O> list = new ArrayList<>();

			for (NextTask<I, O> task : tasks) {
				list.add(task.run(arg));
			}

			return list;
		});
	}

	@SafeVarargs
	public static <I, O> NextTask<O, List<O>> collector(NextTask<Void, O>... tasks) {
		return new NextTask<>((latest) -> {
			List<O> list = new ArrayList<>();
			list.add(latest);
			for (NextTask<Void, O> task : tasks) {
				latest = task.run();
				list.add(latest);
			}
			return list;
		});
	}

	public static <I, O> NextTask<O, List<O>> collector(List<NextTask<Void, O>> tasks) {
		return new NextTask<>((latest) -> {
			List<O> list = new ArrayList<>();
			list.add(latest);
			for (NextTask<Void, O> task : tasks) {
				latest = task.run();
				list.add(latest);
			}
			return list;
		});
	}

	public static <I, O> NextTask<O, List<O>> collector(Stream<NextTask<Void, O>> stream) {
		return collector(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<Void, List<O>> collect(NextTask<Void, O>... tasks) {
		return create(() -> {
			List<O> list = new ArrayList<>();
			for (NextTask<Void, O> task : tasks) {
				list.add(task.run());
			}
			return list;
		});
	}

	public static <O> NextTask<Void, List<O>> collect(List<NextTask<Void, O>> tasks) {
		return create(() -> {
			List<O> list = new ArrayList<>();
			for (NextTask<Void, O> task : tasks) {
				list.add(task.run());
			}
			return list;
		});
	}

	public static <O> NextTask<Void, List<O>> collect(Stream<NextTask<Void, O>> stream) {
		return collect(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<O, List<O>> chain(ExceptionFunction<O, O>... tasks) {
		return new NextTask<>((latest) -> {
			List<O> list = new ArrayList<>();
			list.add(latest);
			for (ExceptionFunction<O, O> task : tasks) {
				latest = task.apply(latest);
				list.add(latest);
			}
			return list;
		});
	}

}