package lu.kbra.pclib.listener;

import java.util.List;
import java.util.function.Consumer;

import lu.kbra.pclib.listener.AbstractEventManager.EventListenerData;

public interface EventManager {

	void close();

	void dispatch(Event evt);

	void dispatch(Event evt, EventDispatcher dispatcher);

	Consumer<Throwable> getExceptionHandler();

	List<EventListenerData> getListeners();

	boolean isClosed();

	AbstractEventManager register(EventListener listener);

	AbstractEventManager unregister(EventListener listener);

}
