package lu.kbra.pclib.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lu.kbra.pclib.impl.ThrowingConsumer;
import lu.kbra.pclib.impl.ThrowingFunction;
import lu.kbra.pclib.impl.ThrowingRunnable;
import lu.kbra.pclib.impl.ThrowingSupplier;

public class NextTask<F, I, O> {

	protected static class NextTaskStatus {
		protected int state = NextTask.IDLE;

		public boolean isDone() {
			return this.state == NextTask.DONE;
		}

		public boolean isError() {
			return this.state == NextTask.ERROR;
		}

		public boolean isRunning() {
			return this.state == NextTask.RUNNING;
		}

		public boolean isIdle() {
			return this.state == NextTask.IDLE;
		}

		public boolean hasEnded() {
			return this.isDone() || this.isError();
		}
	}

	protected static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(100);
	protected static final int IDLE = 0, RUNNING = 1, DONE = 2, ERROR = 3;

	protected final NextTaskStatus sharedState = new NextTaskStatus();

	protected NextTask<F, F, ?> first;
	protected NextTask<F, O, ?> next;

	protected ThrowingConsumer<Throwable, Throwable> catcher;
	protected ThrowingFunction<I, O, Throwable> task;

	protected NextTask(final ThrowingFunction<I, O, Throwable> task) {
		this.task = task;
		this.first = (NextTask<F, F, ?>) this;
	}

	/* ======================== CHAINING ======================== */

	public <N> NextTask<F, O, N> thenApply(final ThrowingFunction<O, N, Throwable> nextFunction) {
		final NextTask<F, O, N> nextTask = new NextTask<>(output -> {
			if (!this.sharedState.isError()) {
				return nextFunction.apply(output);
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	public <N, X> NextTask<F, O, N> thenCompose(final ThrowingFunction<O, NextTask<X, ?, N>, Throwable> nextTaskFunction) {
		final NextTask<F, O, N> nextTask = new NextTask<>(input -> {
			if (!this.sharedState.isError()) {
				final NextTask<X, ?, N> composed = nextTaskFunction.apply(input);
				if (this.hasVoidFirstType(composed)) {
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

	public <N, X> NextTask<F, O, N> thenCompose(final ThrowingSupplier<NextTask<X, ?, N>, Throwable> nextTaskFunction) {
		final NextTask<F, O, N> nextTask = new NextTask<>(input -> {
			if (!this.sharedState.isError()) {
				final NextTask<X, ?, N> composed = nextTaskFunction.get();
				if (this.hasVoidFirstType(composed)) {
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

	public <N> NextTask<F, O, N> thenCompose(final NextTask<O, ?, N> nextTask) {
		final NextTask<F, O, N> next = new NextTask<>(output -> {
			if (!this.sharedState.isError()) {
				return nextTask.runThrow(output);
			}
			return null;
		});
		next.first = this.first;
		this.next = next;
		return next;
	}

	public NextTask<F, O, O> thenParallel(final ThrowingConsumer<O, Throwable> nextFunction) {
		final NextTask<F, O, O> nextTask = new NextTask<>(input -> {
			if (!this.sharedState.isError()) {
				nextFunction.accept(input);
				return input;
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	public NextTask<F, O, Void> thenConsume(final ThrowingConsumer<O, Throwable> consumer) {
		final NextTask<F, O, Void> nextTask = new NextTask<>(output -> {
			if (!this.sharedState.isError()) {
				consumer.accept(output);
			}
			return null;
		});
		nextTask.first = this.first;
		this.next = nextTask;
		return nextTask;
	}

	/* orElses */

	public NextTask<F, O, O> orElse(final O n) {
		return this.thenApply(o -> o == null ? n : o);
	}

	public NextTask<F, O, O> orElseThrow(final Throwable throw_) {
		return this.thenApply((ThrowingFunction<O, O, Throwable>) o -> {
			if (o == null) {
				throw throw_;
			} else {
				return o;
			}
		});
	}

	public NextTask<F, O, O> orElse(final ThrowingSupplier<O, Throwable> n) {
		return this.thenApply(o -> o == null ? n.get() : o);
	}

	public NextTask<F, O, O> orElseThrow(final ThrowingSupplier<Throwable, Throwable> throw_) {
		return this.thenApply((ThrowingFunction<O, O, Throwable>) o -> {
			if (o == null) {
				throw throw_.get();
			} else {
				return o;
			}
		});
	}

	public NextTask<F, O, O> orElse(final NextTask<Void, ?, O> n) {
		return this.thenApply(o -> o == null ? n.run() : o);
	}

	public NextTask<F, O, Optional<O>> toOptional() {
		return this.thenApply(Optional::ofNullable);
	}

	/* exceptions */

	public NextTask<F, I, O> catch_(final ThrowingConsumer<Throwable, Throwable> e) {
		this.catcher = e;
		return this;
	}

	public NextTask<F, I, O> quiet() {
		this.catcher = e -> {
		};
		return this;
	}

	/* ======================== EXECUTION ======================== */

	public O run(final F input) {
		try {
			return this.runThrow(input);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public O run() {
		return this.run(null);
	}

	public O runThrow() throws Throwable {
		return this.runThrow(null);
	}

	@SuppressWarnings("unchecked")
	public O runThrow(final F input) throws Throwable {
		Object result = input;
		NextTask<?, ?, ?> current = this.first;
		this.sharedState.state = NextTask.RUNNING;

		while (current != null) {
			try {
				if (current.task != null && !this.sharedState.isError()) {
					result = ((ThrowingFunction<Object, Object, Throwable>) current.task).apply(result);
				}
				current = current.next;
			} catch (final NextTaskSkip skip) {
				current = this.handleSkip(current, skip);
				result = skip.getObj();
			} catch (final Throwable e) {
				this.sharedState.state = NextTask.ERROR;
				current.propagateException(e);
				return null;
			}
		}

		this.sharedState.state = NextTask.DONE;
		return (O) result;
	}

	private NextTask<?, ?, ?> handleSkip(final NextTask<?, ?, ?> current, final NextTaskSkip skip) {
		int remaining = skip.getCount();
		NextTask<?, ?, ?> target = current.next;

		while (target != null && remaining > 1) {
			target = target.next;
			remaining--;
		}

		if (target != null) {
			if (skip.getNext() != null) {
				final NextTask injected = skip.getNext();
				final NextTask tmp = target.next;
				target.next = injected.first;
				injected.next = tmp;
			}
			return target.next;
		} else {
			final int remainingToSkip = remaining > 0 ? remaining : 0;
			throw new NextTaskSkip(remainingToSkip, skip.getObj(), skip.getNext());
		}
	}

	protected void propagateException(final Throwable e) throws Throwable {
		if (this.catcher != null) {
			this.catcher.accept(e);
		} else if (this.next != null) {
			this.next.propagateException(e);
		} else {
			throw e;
		}
	}

	protected <X> Class<?> getFirstType(final NextTask<X, ?, ?> task) {
		if (task.first != null) {
			final Object firstInput = task.first.task; // task is ThrowingFunction<X,O>
			if (firstInput == null) {
				return Void.class;
			}
		}
		return Void.class; // fallback
	}

	protected <X> boolean hasVoidFirstType(final NextTask<X, ?, ?> composed) {
		return Void.class.equals(this.getFirstType(composed));
	}

	public NextTaskStatus getTaskState() {
		return this.sharedState;
	}

	public NextTaskStatus runAsync(final F input) {
		return this.runAsync(input, NextTask.EXECUTOR);
	}

	public NextTaskStatus runAsync(final F input, final ExecutorService exec) {
		if (!this.sharedState.isIdle()) {
			throw new IllegalStateException("Already running or done");
		}

		exec.submit(() -> this.run(input));
		return this.sharedState;
	}

	public NextTaskStatus runAsync() {
		return this.runAsync((F) null);
	}

	public NextTaskStatus runAsync(final ExecutorService exec) {
		return this.runAsync(null, exec);
	}

	/* static */

	public static <I> NextTask<I, I, Void> withArg(final ThrowingConsumer<I, Throwable> task) {
		return new NextTask<>(a -> {
			task.accept(a);
			return null;
		});
	}

	public static <I, O> NextTask<I, I, O> withArg(final ThrowingFunction<I, O, Throwable> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, Void, O> create(final ThrowingFunction<Void, O, Throwable> task) {
		return new NextTask<>(task);
	}

	public static <O> NextTask<Void, Void, O> create(final ThrowingSupplier<O, Throwable> task) {
		return new NextTask<>(i -> task.get());
	}

	public static NextTask<Void, Void, Void> create(final ThrowingRunnable<Throwable> task) {
		return new NextTask<>(i -> {
			task.run();
			return null;
		});
	}

	public static <O> NextTask<Void, Void, O> empty() {
		return new NextTask<>((final Void v) -> null);
	}

	@SafeVarargs
	public static <I, O> NextTask<I, I, List<O>> parallel(final NextTask<I, ?, O>... tasks) {
		return NextTask.parallel(Arrays.asList(tasks));
	}

	public static <I, O> NextTask<I, I, List<O>> parallel(final List<NextTask<I, ?, O>> tasks) {
		return new NextTask<>(arg -> {
			final List<O> list = new ArrayList<>();

			for (final NextTask<I, ?, O> task : tasks) {
				list.add(task.run(arg));
			}

			return list;
		});
	}

	public static <I, O> NextTask<I, I, List<O>> parallel(final Stream<NextTask<I, ?, O>> tasks) {
		return NextTask.parallel(tasks.collect(Collectors.toList()));
	}

	/** last output + all the new ones */
	@SafeVarargs
	public static <O> NextTask<O, O, List<O>> collector(final NextTask<Void, ?, O>... tasks) {
		return new NextTask<>(latest -> {
			final List<O> list = new ArrayList<>();
			list.add(latest);
			for (final NextTask<Void, ?, O> task : tasks) {
				latest = task.run();
				list.add(latest);
			}
			return list;
		});
	}

	/** last output + all the new ones */
	public static <O> NextTask<O, O, List<O>> collector(final List<NextTask<Void, ?, O>> tasks) {
		return new NextTask<>(latest -> {
			final List<O> list = new ArrayList<>();
			list.add(latest);
			for (final NextTask<Void, ?, O> task : tasks) {
				latest = task.run();
				list.add(latest);
			}
			return list;
		});
	}

	/** last output + all the new ones */
	public static <O> NextTask<O, O, List<O>> collector(final Stream<NextTask<Void, ?, O>> stream) {
		return NextTask.collector(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<Void, Void, List<O>> collect(final NextTask<Void, ?, O>... tasks) {
		return NextTask.collect(Arrays.asList(tasks));
	}

	public static <O> NextTask<Void, Void, List<O>> collect(final List<NextTask<Void, ?, O>> tasks) {
		return NextTask.create(() -> {
			final List<O> list = new ArrayList<>();
			for (final NextTask<Void, ?, O> task : tasks) {
				list.add(task.run());
			}
			return list;
		});
	}

	public static <O> NextTask<Void, Void, List<O>> collect(final Stream<NextTask<Void, ?, O>> stream) {
		return NextTask.collect(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<Void, Void, List<O>> collectSuppliers(final ThrowingSupplier<O, Throwable>... tasks) {
		return NextTask.collectSuppliers(Arrays.asList(tasks));
	}

	public static <O> NextTask<Void, Void, List<O>> collectSuppliers(final List<ThrowingSupplier<O, Throwable>> tasks) {
		return NextTask.create(() -> {
			final List<O> list = new ArrayList<>();
			for (final ThrowingSupplier<O, Throwable> task : tasks) {
				list.add(task.get());
			}
			return list;
		});
	}

	public static <O> NextTask<Void, Void, List<O>> collectSuppliers(final Stream<ThrowingSupplier<O, Throwable>> stream) {
		return NextTask.collectSuppliers(stream.collect(Collectors.toList()));
	}

	@SafeVarargs
	public static <O> NextTask<O, O, List<O>> chain(final ThrowingFunction<O, O, Throwable>... tasks) {
		return new NextTask<>(latest -> {
			final List<O> list = new ArrayList<>();
			list.add(latest);
			for (final ThrowingFunction<O, O, Throwable> task : tasks) {
				latest = task.apply(latest);
				list.add(latest);
			}
			return list;
		});
	}

}
