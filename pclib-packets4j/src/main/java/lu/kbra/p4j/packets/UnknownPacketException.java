package lu.kbra.p4j.packets;

public class UnknownPacketException extends RuntimeException {

	public UnknownPacketException(String msg) {
		super(msg);
	}

	public UnknownPacketException(int id) {
		super("" + id);
	}

}
