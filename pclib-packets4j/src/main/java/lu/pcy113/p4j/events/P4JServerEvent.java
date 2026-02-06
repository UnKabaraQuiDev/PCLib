package lu.pcy113.p4j.events;

import lu.pcy113.p4j.socket.server.ServerClient;

public interface P4JServerEvent extends P4JEvent {

	ServerClient getServerClient();

}
