package lu.kbra.p4j.packets;

public class PacketInstanceException extends Exception {

	private static final long serialVersionUID = 539248252778969485L;

	public PacketInstanceException(final String msg) {
		super(msg);
	}

	public PacketInstanceException(final Exception e, final String msg) {
		super(msg, e);
	}

}
