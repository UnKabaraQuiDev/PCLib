package lu.pcy113.p4j.packets;

import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.ServerClient;

public class HeartbeatPacket implements S2CPacket<Void>, C2SPacket<Void> {

	@Override
	public Void clientWrite(P4JClient client) {
		return null;
	}

	@Override
	public void serverRead(ServerClient sclient, Void obj) {
		
	}

	@Override
	public Void serverWrite(ServerClient client) {
		return null;
	}

	@Override
	public void clientRead(P4JClient client, Void obj) {
		
	}

}
