package lu.kbra.p4j.events.server;

import lu.kbra.p4j.events.P4JEvent;
import lu.kbra.p4j.socket.server.P4JServer;

public class ServerClosedEvent implements P4JEvent {

	private final P4JServer server;

	public ServerClosedEvent(final P4JServer server) {
		this.server = server;
	}

	public P4JServer getServer() {
		return this.server;
	}

}
