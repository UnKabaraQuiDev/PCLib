package catdog;

import java.util.Arrays;
import java.util.Random;

import lu.pcy113.p4j.packets.s2c.S2CPacket;
import lu.pcy113.p4j.socket.client.P4JClient;
import lu.pcy113.p4j.socket.server.ServerClient;
import lu.pcy113.pclib.logger.GlobalLogger;

/**
 * This class is used to manage the communication between the Server → Client<br>
 * It describes how to handle the received String[] and what value to send
 */
public class S2C_CatDogPacket implements S2CPacket<Object[]> {

	/** Gets called when a Client receives this packet from the connected server */
	public void clientRead(P4JClient client, Object[] input) {
		GlobalLogger.info("Question received: ");
		GlobalLogger.info(Arrays.toString(input));
		Random r = new Random();
		int choiceIndex = r.nextInt(input.length);

		// We can cast to a String because we're sure serverWrite() returns String[]
		client.write(new C2S_CatDogPacket((String) input[choiceIndex]));
	}

	/**
	 * Gets called when using ServerClient.write(new S2C_CatDogPacket())<br>
	 * Returns the value to be sent
	 */
	public Object[] serverWrite(ServerClient client) {
		GlobalLogger.info("Asked to client");
		return new String[] { "Dog", "or", "Cat" };
	}
}