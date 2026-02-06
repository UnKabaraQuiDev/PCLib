package lu.pcy113.p4j.packets;

public class PacketInstanceException extends Exception {

	public PacketInstanceException(String msg) {
		super(msg);
	}

	public PacketInstanceException(Exception e, String msg) {
		super(msg, e);
	}

}
