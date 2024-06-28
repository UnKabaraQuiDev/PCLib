package lu.pcy113.pclib.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public abstract class EventManager implements AutoCloseable {

	protected boolean closed = false;

	protected List<EventListenerData> listeners;

	public EventManager(List<EventListenerData> listeners) {
		this.listeners = listeners;
	}

	public EventManager() {
		this(new ArrayList<EventListenerData>());
	}

	protected abstract void dispatch_(Event evt);

	public void dispatch(Event evt) {
		if (closed) {
			throw new EventDispatchException("EventManager's input was closed.");
		}
		dispatch_(evt);
	}

	protected void sortListeners() {
		Collections.sort(listeners);
	}

	public EventManager register(EventListener listener) {
		this.listeners.add(new EventListenerData(listener));
		sortListeners();
		return this;
	}

	public EventManager unregister(EventListener listener) {
		this.listeners.removeIf(data -> data.listener.equals(listener));
		return this;
	}

	public List<EventListenerData> getListeners() {
		return listeners;
	}

	public void setListeners(List<EventListenerData> listeners) {
		this.listeners = listeners;
	}

	@Override
	public void close() {
		closed = true;
		listeners.clear();
		listeners = null;
	}

	protected class EventListenerData implements Comparable<EventListenerData> {

		private final EventListener listener;
		private final int priority;
		private final HashMap<Class<? extends Event>, Method> methods = new HashMap<>();

		@SuppressWarnings("unchecked")
		public EventListenerData(EventListener listener) {
			this.listener = listener;

			final Class<? extends EventListener> listenerClass = listener.getClass();

			if (listenerClass.isAnnotationPresent(ListenerPriority.class)) {
				this.priority = listenerClass.getAnnotation(ListenerPriority.class).priority();
			} else {
				this.priority = 0;
			}

			for (Method m : listenerClass.getMethods()) {
				if (m.getParameterCount() == 0 || !m.isAnnotationPresent(EventHandler.class))
					continue;

				if (!Event.class.isAssignableFrom(m.getParameterTypes()[0]))
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` first parameter isn't of type `Event`.");

				if (m.getParameterCount() == 2 && !m.getParameterTypes()[1].equals(EventManager.class))
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` second parameter isn't of type `EventManager`.");

				if (m.getParameterCount() > 2)
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` awaits too many parameters.");

				if (!Modifier.isPublic(m.getModifiers()))
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` is not accessible.");

				if (!methods.containsKey(m.getParameterTypes()[0])) {
					this.methods.put((Class<? extends Event>) m.getParameterTypes()[0], m);
				} else {
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` redefines the same event listener as `" + this.methods.get(m.getParameterTypes()[0]).getName() + "`.");
				}
			}
		}

		public List<Method> getMethodsFor(Class<? extends Event> eventClass) {
			List<Method> ms = new ArrayList<Method>();

			for (Entry<Class<? extends Event>, Method> m : this.methods.entrySet()) {
				if (m.getKey().isAssignableFrom(eventClass)) {
					ms.add(m.getValue());
				}
			}

			return ms;
		}

		public EventListener getListener() {
			return listener;
		}

		public int getPriority() {
			return priority;
		}

		public HashMap<Class<? extends Event>, Method> getMethods() {
			return methods;
		}

		@Override
		public int compareTo(EventListenerData o) {
			return Integer.compare(o.priority, this.priority);
		}

	}

}
