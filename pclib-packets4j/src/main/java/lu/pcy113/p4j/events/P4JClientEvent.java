package lu.pcy113.p4j.events;

import lu.pcy113.p4j.socket.client.P4JClient;

public interface P4JClientEvent extends P4JEvent {

	P4JClient getClient();
	
}
