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
	protected void dispatch_(Event event) {
		final Class<? extends Event> eventClass = event.getClass();
		for (EventListenerData listenerData : listeners) {
			final List<Method> methods = listenerData.getMethodsFor(eventClass);
			for (Method method : methods) {
				try {
					if (method.getParameterCount() == 2) {
						method.invoke(listenerData.getListener(), event, this);
					} else {
						method.invoke(listenerData.getListener(), event);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new EventDispatchException(e);
				}
			}
		}
	}

}
