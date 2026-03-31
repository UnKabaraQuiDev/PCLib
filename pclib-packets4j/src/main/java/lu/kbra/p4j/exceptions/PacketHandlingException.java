package lu.kbra.p4j.exceptions;

public class PacketHandlingException extends P4JException {

	private static final long serialVersionUID = 2889179619164335697L;

	public PacketHandlingException(final int id, final Exception e) {
		super("Error while handling packet: " + id, e);
	}

}
