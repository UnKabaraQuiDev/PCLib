package lu.kbra.p4j.packets;

import lu.kbra.p4j.packets.c2s.C2SPacket;
import lu.kbra.p4j.packets.s2c.S2CPacket;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.ServerClient;

public class HeartbeatPacket implements S2CPacket<Void>, C2SPacket<Void> {

	@Override
	public Void clientWrite(final P4JClient client) {
		return null;
	}

	@Override
	public void serverRead(final ServerClient sclient, final Void obj) {

	}

	@Override
	public Void serverWrite(final ServerClient client) {
		return null;
	}

	@Override
	public void clientRead(final P4JClient client, final Void obj) {

	}

}
