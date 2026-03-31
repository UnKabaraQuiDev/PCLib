package lu.kbra.p4j.exceptions;

public class P4JMaxPacketSizeExceeded extends P4JException {

	private static final long serialVersionUID = 7940996082577821540L;

	public P4JMaxPacketSizeExceeded(final int size) {
		super("Exceeded max packet size: " + size);
	}

	public P4JMaxPacketSizeExceeded(final int size, final Throwable th) {
		super("Exceeded max packet size: " + size, th);
	}

	public P4JMaxPacketSizeExceeded(final Throwable th) {
		super(th);
	}

}
