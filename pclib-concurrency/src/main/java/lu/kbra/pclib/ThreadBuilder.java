package lu.kbra.pclib;

import java.lang.Thread.UncaughtExceptionHandler;

import lu.kbra.pclib.impl.ThrowingRunnable;

public class ThreadBuilder {

	private final Thread thread;

	private ThreadBuilder(final Runnable run) {
		this.thread = new Thread(run);
	}

	private ThreadBuilder(final ThreadGroup group, final Runnable run) {
		this.thread = new Thread(group, run);
	}

	private ThreadBuilder(final ThrowingRunnable<Throwable> run) {
		this.thread = new Thread(() -> {
			try {
				run.run();
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	private ThreadBuilder(final ThreadGroup group, final ThrowingRunnable<Throwable> run) {
		this.thread = new Thread(group, () -> {
			try {
				run.run();
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	public ThreadBuilder daemon(final boolean daemon) {
		this.thread.setDaemon(daemon);
		return this;
	}

	public ThreadBuilder name(final String name) {
		this.thread.setName(name);
		return this;
	}

	public ThreadBuilder priority(final int priority) {
		this.thread.setPriority(priority);
		return this;
	}

	public ThreadBuilder except(final UncaughtExceptionHandler eh) {
		this.thread.setUncaughtExceptionHandler(eh);
		return this;
	}

	public Thread start() {
		this.thread.start();
		return this.thread;
	}

	public Thread build() {
		return this.thread;
	}

	public static ThreadBuilder create(final ThreadGroup group, final Runnable run) {
		return new ThreadBuilder(group, run);
	}

	public static ThreadBuilder create(final ThreadGroup group, final ThrowingRunnable<Throwable> run) {
		return new ThreadBuilder(group, run);
	}

	public static ThreadBuilder create(final Runnable run) {
		return new ThreadBuilder(run);
	}

	public static ThreadBuilder create(final ThrowingRunnable<Throwable> run) {
		return new ThreadBuilder(run);
	}

}
