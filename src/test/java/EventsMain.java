import java.io.IOException;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.listener.AsyncEventManager;
import lu.pcy113.pclib.listener.Event;
import lu.pcy113.pclib.listener.EventDispatcher;
import lu.pcy113.pclib.listener.EventHandler;
import lu.pcy113.pclib.listener.EventListener;
import lu.pcy113.pclib.listener.EventManager;
import lu.pcy113.pclib.listener.ListenerPriority;
import lu.pcy113.pclib.listener.SyncEventManager;

public class EventsMain {

	@Test
	public void treeTest() throws IOException {
		System.out.println(PCUtils.recursiveTree("./src"));
	}

	@Test
	public void eventTest() {
		EventManager manager = new SyncEventManager();

		System.out.println("reg");
		manager.register(new EventListenerExample());
		manager.register(new EventListenerExample2());

		manager.dispatch(new SubEvent());

		manager = new AsyncEventManager(5);

		System.out.println("reg");
		manager.register(new EventListenerExample());
		manager.register(new EventListenerExample2());

		manager.dispatch(new SubEvent());
	}

	public static class SubEvent implements Event {

	}

	@ListenerPriority(priority = 0)
	public static class EventListenerExample implements EventListener {

		@EventHandler
		public void onEvent(Event event, EventManager man, EventDispatcher dispatcher) {
			System.out.println("ex1 got: " + event.getClass().getName() + " from: " + Thread.currentThread().getName() + " && " + man.getClass().getName() + " & " + dispatcher);
		}

		@EventHandler
		public void onEvent2(SubEvent event, EventManager man, EventDispatcher dispatcher) {
			System.out.println("ex2 got: " + event.getClass().getName() + " from: " + Thread.currentThread().getName() + " & " + dispatcher);
		}

	}

	@ListenerPriority(priority = 10)
	public static class EventListenerExample2 implements EventListener {

		@EventHandler
		public void onEvent(Event event, EventManager man, EventDispatcher dispatcher) {
			System.out.println("ex2.1 got: " + event.getClass().getName() + " from: " + Thread.currentThread().getName() + " & " + dispatcher);
		}

		@EventHandler
		public void onEvent2(SubEvent event, EventManager man, EventDispatcher dispatcher) {
			System.out.println("ex2.2 got: " + event.getClass().getName() + " from: " + Thread.currentThread().getName() + " & " + dispatcher);
		}

	}

}
