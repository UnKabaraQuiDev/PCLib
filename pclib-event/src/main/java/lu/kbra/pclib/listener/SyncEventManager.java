package lu.kbra.pclib.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SyncEventManager extends AbstractEventManager {

	public SyncEventManager() {
	}

	public SyncEventManager(final List<EventListener> listeners) {
		super(listeners);
	}

	@Override
	protected void dispatch_(final Event event, final EventDispatcher dispatcher) {
		final Class<? extends Event> eventClass = event.getClass();
		for (final EventListenerData listenerData : this.listeners) {
			final List<Method> methods = listenerData.getMethodsFor(eventClass);
			for (final Method method : methods) {
				try {
					if (method.getParameterCount() == 1) {
						method.invoke(listenerData.getListener(), event);
					} else if (method.getParameterCount() == 2) {
						method.invoke(listenerData.getListener(), event, this);
					} else if (method.getParameterCount() == 3) {
						method.invoke(listenerData.getListener(), event, this, dispatcher);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					this.exceptionHandler.accept(e);
				}
			}
		}
	}

}
