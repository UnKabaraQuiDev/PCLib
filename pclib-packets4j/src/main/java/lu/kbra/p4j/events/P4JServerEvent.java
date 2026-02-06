package lu.kbra.p4j.events;

import lu.kbra.p4j.socket.server.ServerClient;

public interface P4JServerEvent extends P4JEvent {

	ServerClient getServerClient();

}
