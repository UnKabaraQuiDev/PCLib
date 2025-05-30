package lu.pcy113.pclib.builder;

import java.lang.Thread.UncaughtExceptionHandler;

public class ThreadBuilder {

	private Thread thread;

	private ThreadBuilder(Runnable run) {
		thread = new Thread(run);
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

	public static ThreadBuilder create(Runnable run) {
		return new ThreadBuilder(run);
	}

}
