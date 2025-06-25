package lu.pcy113.pclib.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lu.pcy113.pclib.builder.ThreadBuilder;

public class AsyncEventManager extends EventManager {

	private ExecutorService executor;

	/**
	 * Fixed single thread pool executor
	 * 
	 * @param variable If the execution pool has a variable thread count
	 */
	public AsyncEventManager(boolean variable, boolean daemons) {
		super();
		if (variable) {
			executor = Executors.newCachedThreadPool((r) -> ThreadBuilder.create(r).daemon(daemons).build());
		} else {
			executor = Executors.newSingleThreadExecutor((r) -> ThreadBuilder.create(r).daemon(daemons).build());
		}
	}

	public AsyncEventManager(List<EventListener> listeners, boolean variable, boolean daemons) {
		super(listeners);
		if (variable) {
			executor = Executors.newCachedThreadPool((r) -> ThreadBuilder.create(r).daemon(daemons).build());
		} else {
			executor = Executors.newSingleThreadExecutor((r) -> ThreadBuilder.create(r).daemon(daemons).build());
		}
	}

	public AsyncEventManager(int poolSize, boolean daemons) {
		super();
		executor = Executors.newFixedThreadPool(poolSize, (r) -> ThreadBuilder.create(r).daemon(daemons).build());
	}

	public AsyncEventManager(boolean variable) {
		this(variable, true);
	}

	public AsyncEventManager(int poolSize) {
		this(poolSize, true);
	}

	@Override
	protected void dispatch_(Event event, EventDispatcher dispatcher) {
		final Class<? extends Event> eventClass = event.getClass();

		for (EventListenerData listenerData : listeners) {
			final List<Method> methods = listenerData.getMethodsFor(eventClass);
			for (Method method : methods) {
				executor.execute(() -> {
					try {
						if (method.getParameterCount() == 1) {
							method.invoke(listenerData.getListener(), event);
						} else if (method.getParameterCount() == 2) {
							method.invoke(listenerData.getListener(), event, this);
						} else if (method.getParameterCount() == 3) {
							method.invoke(listenerData.getListener(), event, this, dispatcher);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						exceptionHandler.accept(e);
					}
				});
			}
		}
	}

	@Override
	public void close() {
		super.close();
		// executor.shutdown();
		executor.shutdownNow();
	}

}
