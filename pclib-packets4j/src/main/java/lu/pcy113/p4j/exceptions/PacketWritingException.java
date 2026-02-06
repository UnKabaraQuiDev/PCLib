package lu.pcy113.p4j.exceptions;

import lu.pcy113.p4j.packets.c2s.C2SPacket;
import lu.pcy113.pclib.PCUtils;

public class PacketWritingException extends P4JException {

	public PacketWritingException(C2SPacket packet, Throwable e) {
		super("Error while writing packet: " + PCUtils.try_(() -> packet.getClass().getName(), (ex) -> "<" + ex.getMessage() + ">"), e);
	}

}
