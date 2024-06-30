package lu.pcy113.pclib.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SyncEventManager extends EventManager {

	public SyncEventManager() {
		super();
	}

	public SyncEventManager(List<EventListenerData> listeners) {
		super(listeners);
	}

	@Override
	protected void dispatch_(Event event, EventDispatcher dispatcher) {
		final Class<? extends Event> eventClass = event.getClass();
		for (EventListenerData listenerData : listeners) {
			final List<Method> methods = listenerData.getMethodsFor(eventClass);
			for (Method method : methods) {
				try {
					if (method.getParameterCount() == 1) {
						method.invoke(listenerData.getListener(), event);
					} else if (method.getParameterCount() == 2) {
						method.invoke(listenerData.getListener(), event, this);
					} else if (method.getParameterCount() == 3) {
						method.invoke(listenerData.getListener(), event, this, dispatcher);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new EventDispatchException(e);
				}
			}
		}
	}

}
