package lu.pcy113.pclib.listener;

public interface IEventManager {

	void dispatch(Event evt);

	void dispatch(Event evt, EventDispatcher dispatcher);

}
