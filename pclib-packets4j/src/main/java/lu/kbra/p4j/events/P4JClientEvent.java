package lu.kbra.p4j.events;

import lu.kbra.p4j.socket.client.P4JClient;

public interface P4JClientEvent extends P4JEvent {

	P4JClient getClient();
	
}
