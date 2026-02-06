package catdog;

import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.ServerClient;
import lu.pcy113.pclib.logger.GlobalLogger;

/**
 * This class is used to manage the communication between the Client → Server<br>
 * It describes how to handle the received String and what value to send
 */
public class C2S_CatDogPacket implements C2SPacket<String> {
	String choice;

	/**
	 * A constructor with no argument is needed or a PacketInstanceException will be thrown
	 */
	public C2S_CatDogPacket() {
	}

	public C2S_CatDogPacket(String choice) {
		GlobalLogger.info("Choice prepared: " + choice);
		this.choice = choice;
	}

	/** Gets called when using P4JClient.write(new C2S_CatDogPacket()) Returns the value to be sent */
	public String clientWrite(P4JClient client) {
		GlobalLogger.info("Responding to server: " + choice);
		return this.choice;
	}

	/** Gets called when a Server receives this packet from a connected Client */
	public void serverRead(ServerClient sclient, String obj) {
		GlobalLogger.info("Client answered: " + obj);
	}
}