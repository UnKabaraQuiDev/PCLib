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

public class NextTask<F, I, O> {

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
	protected static final int IDLE = 0, RUNNING = 1, DONE = 2, ERROR = 3;

	protected final NextTaskStatus sharedState = new NextTaskStatus();

	protected NextTask<F, F, ?> first;
	protected NextTask<F, O, ?> next;

	protected ExceptionConsumer<Throwable> catcher;
	protected ExceptionFunction<I, O> task;

	protected NextTask(ExceptionFunction<I, O> task) {
		this.task = task;
		this.first = (NextTask<F, F, ?>) this;
	}

	/* ======================== CHAINING ======================== */

	public <N> NextTask<F, O, N> thenApply(ExceptionFunction<O, N> nextFunction) {
		NextTask<F, O, N> nextTask = new NextTask<>(output -> {
			if (!sharedState.isError()) {
				return nextFunction.apply(output);
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	public <N, X> NextTask<F, O, N> thenCompose(ExceptionFunction<O, NextTask<X, ?, N>> nextTaskFunction) {
		NextTask<F, O, N> nextTask = new NextTask<>(input -> {
			if (!sharedState.isError()) {
				NextTask<X, ?, N> composed = nextTaskFunction.apply(input);
				if (hasVoidFirstType(composed)) {
					return composed.runThrow();
				} else {
					return composed.runThrow((X) input);
				}
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	public <N, X> NextTask<F, O, N> thenCompose(ExceptionSupplier<NextTask<X, ?, N>> nextTaskFunction) {
		NextTask<F, O, N> nextTask = new NextTask<>(input -> {
			if (!sharedState.isError()) {
				NextTask<X, ?, N> composed = nextTaskFunction.get();
				if (hasVoidFirstType(composed)) {
					return composed.runThrow();
				} else {
					return composed.runThrow((X) input);
				}
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	public <N> NextTask<F, O, N> thenCompose(NextTask<O, ?, N> nextTask) {
		NextTask<F, O, N> next = new NextTask<>(output -> {
			if (!sharedState.isError()) {
				return nextTask.runThrow((O) output);
			}
			return null;
		});
		next.first = this.first;
		this.next = next;
		return next;
	}

	public NextTask<F, O, O> thenParallel(ExceptionConsumer<O> nextFunction) {
		NextTask<F, O, O> nextTask = new NextTask<>(input -> {
			if (!sharedState.isError()) {
				nextFunction.accept(input);
				return input;
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	public NextTask<F, O, Void> thenConsume(ExceptionConsumer<O> consumer) {
		NextTask<F, O, Void> nextTask = new NextTask<>(output -> {
			if (!sharedState.isError()) {
				consumer.accept(output);
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	/* orElses */

	public NextTask<F, O, O> orElse(O n) {
		return thenApply(o -> o == null ? n : o);
	}

	public NextTask<F, O, O> orElseThrow(Throwable throw_) {
		return thenApply((ExceptionFunction<O, O>) o -> {
			if (o == null) {
				throw throw_;
			} else {
				return o;
			}
		});
	}

	public NextTask<F, O, O> orElse(ExceptionSupplier<O> n) {
		return thenApply(o -> o == null ? n.get() : o);
	}

	public NextTask<F, O, O> orElseThrow(ExceptionSupplier<Throwable> throw_) {
		return thenApply((ExceptionFunction<O, O>) o -> {
			if (o == null) {
				throw throw_.get();
			} else {
				return o;
			}
		});
	}

	public NextTask<F, O, O> orElse(NextTask<Void, ?, O> n) {
		return thenApply(o -> o == null ? n.run() : o);
	}

	public NextTask<F, O, Optional<O>> toOptional() {
		return thenApply(o -> Optional.ofNullable(o));
	}

	/* exceptions */

	public NextTask<F, I, O> catch_(ExceptionConsumer<Throwable> e) {
		this.catcher = e;
		return this;
	}

	public NextTask<F, I, O> quiet() {
		this.catcher = e -> {
		};
		return this;
	}

	/* ======================== EXECUTION ======================== */

	public O run(F input) {
		try {
			return runThrow(input);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public O run() {
		return run(null);
	}

	public O runThrow() throws Throwable {
		return runThrow(null);
	}

	@SuppressWarnings("unchecked")
	public O runThrow(F input) throws Throwable {
		Object result = input;
		NextTask<?, ?, ?> current = first;
		sharedState.state = RUNNING;

		while (current != null) {
			try {
				if (current.task != null && !sharedState.isError()) {
					result = ((ExceptionFunction<Object, Object>) current.task).apply(result);
				}
				current = current.next;
			} catch (NextTaskSkip skip) {
				current = handleSkip(current, skip);
				result = skip.getObj();
			} catch (Throwable e) {
				sharedState.state = ERROR;
				current.propagateException(e);
				return null;
			}
		}

		sharedState.state = DONE;
		return (O) result;
	}

	private NextTask<?, ?, ?> handleSkip(NextTask<?, ?, ?> current, NextTaskSkip skip) {
		int remaining = skip.getCount();
		NextTask<?, ?, ?> target = current.next;

		while (target != null && remaining > 1) {
			target = target.next;
			remaining--;
		}

		if (target != null) {
			if (skip.getNext() != null) {
				NextTask injected = skip.getNext();
				NextTask tmp = target.next;
				target.next = injected.first;
				injected.next = tmp;
			}
			return target.next;
		} else {
			int remainingToSkip = remaining > 0 ? remaining : 0;
			throw new NextTaskSkip(remainingToSkip, skip.getObj(), skip.getNext());
		}
	}

	protected void propagateException(Throwable e) throws Throwable {
		if (catcher != null) {
			catcher.accept(e);
		} else if (next != null) {
			next.propagateException(e);
		} else {
			throw e;
		}
	}

	protected <X> Class<?> getFirstType(NextTask<X, ?, ?> task) {
		if (task.first != null) {
			Object firstInput = task.first.task; // task is ExceptionFunction<X,O>
			if (firstInput == null)
				return Void.class;
		}
		return Void.class; // fallback
	}

	protected <X> boolean hasVoidFirstType(NextTask<X, ?, ?> composed) {
		return Void.class.equals(getFirstType(composed));
	}

	public NextTaskStatus getTaskState() {
		return sharedState;
	}

	public NextTaskStatus runAsync(F input) {
		if (!sharedState.isIdle()) {
			throw new IllegalStateException("Already running or done");
		}

		EXECUTOR.submit(() -> run(input));
		return sharedState;
	}

	public NextTaskStatus runAsync() {
		return runAsync(null);
	}

	/* static */

	public static <I> NextTask<I, I, Void> withArg(ExceptionConsumer<I> task) {
		return new NextTask<>((a) -> {
			task.accept(a);
			return null;
		});
	}

	public static <I, O> NextTask<I, I, O> withArg(ExceptionFunction<I, O> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, Void, O> create(ExceptionFunction<Void, O> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, Void, O> create(ExceptionSupplier<O> task) {
		return new NextTask<>((i) -> task.get());
	}

	public static NextTask<Void, Void, Void> create(ExceptionRunnable task) {
		return new NextTask<>((i) -> {
			task.run();
			return null;
		});
	}

	public static <O> NextTask<Void, Void, O> empty() {
		return new NextTask<>((Void v) -> null);
	}

	@SafeVarargs
	public static <I, O> NextTask<I, I, List<O>> parallel(NextTask<I, ?, O>... tasks) {
		return parallel(PCUtils.asArrayList(tasks));
	}

	public static <I, O> NextTask<I, I, List<O>> parallel(List<NextTask<I, ?, O>> tasks) {
		return new NextTask<>((arg) -> {
			List<O> list = new ArrayList<>();

			for (NextTask<I, ?, O> task : tasks) {
				list.add(task.run(arg));
			}

			return list;
		});
	}

	public static <I, O> NextTask<I, I, List<O>> parallel(Stream<NextTask<I, ?, O>> tasks) {
		return parallel(tasks.collect(Collectors.toList()));
	}

	/** last output + all the new ones */
	@SafeVarargs
	public static <O> NextTask<O, O, List<O>> collector(NextTask<Void, ?, O>... tasks) {
		return new NextTask<>((latest) -> {
			List<O> list = new ArrayList<>();
			list.add(latest);
			for (NextTask<Void, ?, O> task : tasks) {
				latest = task.run();
				list.add(latest);
			}
			return list;
		});
	}

	/** last output + all the new ones */
	public static <O> NextTask<O, O, List<O>> collector(List<NextTask<Void, ?, O>> tasks) {
		return new NextTask<>((latest) -> {
			List<O> list = new ArrayList<>();
			list.add(latest);
			for (NextTask<Void, ?, O> task : tasks) {
				latest = task.run();
				list.add(latest);
			}
			return list;
		});
	}

	/** last output + all the new ones */
	public static <O> NextTask<O, O, List<O>> collector(Stream<NextTask<Void, ?, O>> stream) {
		return collector(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<Void, Void, List<O>> collect(NextTask<Void, ?, O>... tasks) {
		return collect(PCUtils.asArrayList(tasks));
	}

	public static <O> NextTask<Void, Void, List<O>> collect(List<NextTask<Void, ?, O>> tasks) {
		return create(() -> {
			List<O> list = new ArrayList<>();
			for (NextTask<Void, ?, O> task : tasks) {
				list.add(task.run());
			}
			return list;
		});
	}

	public static <O> NextTask<Void, Void, List<O>> collect(Stream<NextTask<Void, ?, O>> stream) {
		return collect(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<Void, Void, List<O>> collectSuppliers(ExceptionSupplier<O>... tasks) {
		return collectSuppliers(PCUtils.asArrayList(tasks));
	}

	public static <O> NextTask<Void, Void, List<O>> collectSuppliers(List<ExceptionSupplier<O>> tasks) {
		return create(() -> {
			List<O> list = new ArrayList<>();
			for (ExceptionSupplier<O> task : tasks) {
				list.add(task.get());
			}
			return list;
		});
	}

	public static <O> NextTask<Void, Void, List<O>> collectSuppliers(Stream<ExceptionSupplier<O>> stream) {
		return collectSuppliers(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<O, O, List<O>> chain(ExceptionFunction<O, O>... tasks) {
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