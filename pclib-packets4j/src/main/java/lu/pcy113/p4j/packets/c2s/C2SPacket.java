package lu.pcy113.p4j.packets.c2s;

import lu.pcy113.p4j.packets.Packet;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.ServerClient;

public interface C2SPacket<T> extends Packet {

	T clientWrite(P4JClient client);

	void serverRead(ServerClient sclient, T obj);

}