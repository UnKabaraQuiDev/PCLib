package lu.kbra.pclib.listener;

import java.util.List;
import java.util.function.Consumer;

import lu.kbra.pclib.listener.AbstractEventManager.EventListenerData;

public interface EventManager {

	void dispatch(Event evt);

	void dispatch(Event evt, EventDispatcher dispatcher);

	void close();

	boolean isClosed();

	Consumer<Throwable> getExceptionHandler();

	List<EventListenerData> getListeners();

	AbstractEventManager unregister(EventListener listener);

	AbstractEventManager register(EventListener listener);

}
