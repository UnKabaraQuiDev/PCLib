package lu.kbra.p4j.exceptions;

public class PacketHandlingException extends P4JException {

	public PacketHandlingException(int id, Exception e) {
		super("Error while handling packet: " + id, e);
	}

}
