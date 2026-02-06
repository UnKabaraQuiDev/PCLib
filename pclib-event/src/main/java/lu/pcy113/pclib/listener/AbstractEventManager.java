package lu.pcy113.pclib.listener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

public abstract class AbstractEventManager implements AutoCloseable, EventManager {

	protected boolean closed = false;

	protected List<EventListenerData> listeners = new ArrayList<>();

	protected Consumer<Throwable> exceptionHandler = Throwable::printStackTrace;

	public AbstractEventManager(List<EventListener> listeners) {
		listeners.forEach(this::register);
	}

	public AbstractEventManager() {
	}

	protected abstract void dispatch_(Event evt, EventDispatcher dispatcher);

	public void dispatch(Event evt) {
		if (closed) {
			throw new EventDispatchException("EventManager's input was closed.");
		}
		dispatch_(evt, null);
	}

	public void dispatch(Event evt, EventDispatcher dispatcher) {
		if (closed) {
			throw new EventDispatchException("EventManager's input was closed.");
		}
		dispatch_(evt, dispatcher);
	}

	protected void sortListeners() {
		Collections.sort(listeners);
	}

	public AbstractEventManager register(EventListener listener) {
		this.listeners.add(new EventListenerData(listener, getClassFor(listener)));
		sortListeners();
		return this;
	}

	protected Class<? extends EventListener> getClassFor(EventListener listener) {
		return listener.getClass();
	}

	public AbstractEventManager unregister(EventListener listener) {
		this.listeners.removeIf(data -> data.listener.equals(listener));
		return this;
	}

	public List<EventListenerData> getListeners() {
		return listeners;
	}

	public void setListeners(List<EventListenerData> listeners) {
		this.listeners = listeners;
	}

	public Consumer<Throwable> getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(Consumer<Throwable> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public boolean isClosed() {
		return closed;
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
		public EventListenerData(EventListener listener, Class<? extends EventListener> listenerClass) {
			this.listener = listener;

			if (listenerClass.isAnnotationPresent(ListenerPriority.class)) {
				this.priority = listenerClass.getAnnotation(ListenerPriority.class).priority();
			} else {
				this.priority = 0;
			}

			for (Method m : listenerClass.getMethods()) {
				if (m.getParameterCount() == 0 || !m.isAnnotationPresent(EventHandler.class))
					continue;

				if (!Event.class.isAssignableFrom(m.getParameterTypes()[0]))
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName()
							+ "` first parameter isn't of type `Event`.");

				if (m.getParameterCount() == 2 && !m.getParameterTypes()[1].equals(AbstractEventManager.class))
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName()
							+ "` second parameter isn't of type `EventManager`.");

				if (m.getParameterCount() == 3 && !EventDispatcher.class.isAssignableFrom(m.getParameterTypes()[2]))
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName()
							+ "` third parameter isn't of type `EventDispatcher`.");

				if (m.getParameterCount() > 3)
					throw new IllegalArgumentException(
							"@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` awaits too many parameters.");

				if (!Modifier.isPublic(m.getModifiers()))
					throw new IllegalArgumentException(
							"@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName() + "` is not accessible.");

				if (!methods.containsKey(m.getParameterTypes()[0])) {
					this.methods.put((Class<? extends Event>) m.getParameterTypes()[0], m);
				} else {
					throw new IllegalArgumentException("@EventHandler Method `" + m.getName() + "` in `" + listenerClass.getName()
							+ "` redefines the same event listener as `" + this.methods.get(m.getParameterTypes()[0]).getName() + "`.");
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
