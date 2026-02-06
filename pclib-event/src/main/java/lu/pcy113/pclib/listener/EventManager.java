package lu.pcy113.pclib.listener;

public interface EventManager {

	void dispatch(Event evt);

	void dispatch(Event evt, EventDispatcher dispatcher);

}
