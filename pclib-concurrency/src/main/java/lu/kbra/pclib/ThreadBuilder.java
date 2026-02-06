package lu.kbra.pclib;

import java.lang.Thread.UncaughtExceptionHandler;

import lu.kbra.pclib.impl.ThrowingRunnable;

public class ThreadBuilder {

	private Thread thread;

	private ThreadBuilder(Runnable run) {
		thread = new Thread(run);
	}

	private ThreadBuilder(ThreadGroup group, Runnable run) {
		thread = new Thread(group, run);
	}

	private ThreadBuilder(ThrowingRunnable<Throwable> run) {
		thread = new Thread(() -> {
			try {
				run.run();
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	private ThreadBuilder(ThreadGroup group, ThrowingRunnable<Throwable> run) {
		thread = new Thread(group, () -> {
			try {
				run.run();
			} catch (RuntimeException re) {
				throw re;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	public ThreadBuilder daemon(boolean daemon) {
		thread.setDaemon(daemon);
		return this;
	}

	public ThreadBuilder name(String name) {
		thread.setName(name);
		return this;
	}

	public ThreadBuilder priority(int priority) {
		thread.setPriority(priority);
		return this;
	}

	public ThreadBuilder except(UncaughtExceptionHandler eh) {
		thread.setUncaughtExceptionHandler(eh);
		return this;
	}

	public Thread start() {
		thread.start();
		return thread;
	}

	public Thread build() {
		return thread;
	}

	public static ThreadBuilder create(ThreadGroup group, Runnable run) {
		return new ThreadBuilder(group, run);
	}

	public static ThreadBuilder create(ThreadGroup group, ThrowingRunnable<Throwable> run) {
		return new ThreadBuilder(group, run);
	}

	public static ThreadBuilder create(Runnable run) {
		return new ThreadBuilder(run);
	}

	public static ThreadBuilder create(ThrowingRunnable<Throwable> run) {
		return new ThreadBuilder(run);
	}

}
