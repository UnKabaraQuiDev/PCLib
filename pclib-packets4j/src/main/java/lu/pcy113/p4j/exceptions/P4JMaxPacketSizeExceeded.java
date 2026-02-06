package lu.pcy113.p4j.exceptions;

public class P4JMaxPacketSizeExceeded extends P4JException {

	public P4JMaxPacketSizeExceeded(int size) {
		super("Exceeded max packet size: " + size);
	}

	public P4JMaxPacketSizeExceeded(int size, Throwable th) {
		super("Exceeded max packet size: " + size, th);
	}

	public P4JMaxPacketSizeExceeded(Throwable th) {
		super(th);
	}

}
