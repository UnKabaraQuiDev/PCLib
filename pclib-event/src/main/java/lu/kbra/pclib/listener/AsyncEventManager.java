package lu.kbra.pclib.listener;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.kbra.pclib.ThreadBuilder;

public class AsyncEventManager extends AbstractEventManager {

	private ExecutorService executor;

	/**
	 * Fixed single thread pool executor
	 *
	 * @param variable If the execution pool has a variable thread count
	 */
	public AsyncEventManager(final boolean variable, final boolean daemons) {
		if (variable) {
			this.executor = Executors.newCachedThreadPool(r -> ThreadBuilder.create(r).daemon(daemons).build());
		} else {
			this.executor = Executors.newSingleThreadExecutor(r -> ThreadBuilder.create(r).daemon(daemons).build());
		}
	}

	public AsyncEventManager(final List<EventListener> listeners, final boolean variable, final boolean daemons) {
		super(listeners);
		if (variable) {
			this.executor = Executors.newCachedThreadPool(r -> ThreadBuilder.create(r).daemon(daemons).build());
		} else {
			this.executor = Executors.newSingleThreadExecutor(r -> ThreadBuilder.create(r).daemon(daemons).build());
		}
	}

	public AsyncEventManager(final int poolSize, final boolean daemons) {
		this.executor = Executors.newFixedThreadPool(poolSize, r -> ThreadBuilder.create(r).daemon(daemons).build());
	}

	public AsyncEventManager(final boolean variable) {
		this(variable, true);
	}

	public AsyncEventManager(final int poolSize) {
		this(poolSize, true);
	}

	@Override
	protected void dispatch_(final Event event, final EventDispatcher dispatcher) {
		final Exception source = new Exception();
		source.fillInStackTrace();

		final Class<? extends Event> eventClass = event.getClass();

		for (final EventListenerData listenerData : this.listeners) {
			final List<Method> methods = listenerData.getMethodsFor(eventClass);
			for (final Method method : methods) {
				this.executor.execute(() -> {
					try {
						if (method.getParameterCount() == 1) {
							method.invoke(listenerData.getListener(), event);
						} else if (method.getParameterCount() == 2) {
							method.invoke(listenerData.getListener(), event, this);
						} else if (method.getParameterCount() == 3) {
							method.invoke(listenerData.getListener(), event, this, dispatcher);
						}
					} catch (final Throwable e) {
						e.addSuppressed(source);
						this.exceptionHandler.accept(e);
					}
				});
			}
		}
	}

	@Override
	public void close() {
		super.close();
		// executor.shutdown();
		this.executor.shutdownNow();
	}

}
