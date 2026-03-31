package lu.kbra.p4j.exceptions;

public class P4JClientException extends P4JException {

	private static final long serialVersionUID = -5586870542799399000L;

	public P4JClientException() {
	}

	public P4JClientException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JClientException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JClientException(final String arg0) {
		super(arg0);
	}

	public P4JClientException(final Throwable arg0) {
		super(arg0);
	}

}
