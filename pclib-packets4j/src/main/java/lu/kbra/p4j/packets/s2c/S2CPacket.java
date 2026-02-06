package lu.kbra.p4j.packets.s2c;

import lu.kbra.p4j.packets.Packet;
import lu.kbra.p4j.socket.client.P4JClient;
import lu.kbra.p4j.socket.server.ServerClient;

public interface S2CPacket<T> extends Packet {

	T serverWrite(ServerClient client);

	void clientRead(P4JClient client, T obj);

}