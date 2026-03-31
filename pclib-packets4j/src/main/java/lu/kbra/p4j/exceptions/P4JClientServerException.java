package lu.kbra.p4j.exceptions;

public class P4JClientServerException extends P4JClientException {

	private static final long serialVersionUID = 6582872029815587125L;

	public P4JClientServerException() {
	}

	public P4JClientServerException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JClientServerException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JClientServerException(final String arg0) {
		super(arg0);
	}

	public P4JClientServerException(final Throwable arg0) {
		super(arg0);
	}

}
