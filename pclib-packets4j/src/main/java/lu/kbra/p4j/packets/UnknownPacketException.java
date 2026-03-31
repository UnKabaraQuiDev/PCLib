package lu.kbra.p4j.packets;

public class UnknownPacketException extends RuntimeException {

	private static final long serialVersionUID = -1480759766968737407L;

	public UnknownPacketException(final String msg) {
		super(msg);
	}

	public UnknownPacketException(final int id) {
		super("" + id);
	}

}
