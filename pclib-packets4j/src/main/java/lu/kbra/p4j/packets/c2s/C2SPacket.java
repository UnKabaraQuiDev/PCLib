package lu.kbra.p4j.packets.c2s;

import lu.kbra.p4j.packets.Packet;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.ServerClient;

public interface C2SPacket<T> extends Packet {

	T clientWrite(P4JClient client);

	void serverRead(ServerClient sclient, T obj);

}