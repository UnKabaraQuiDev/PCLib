package lu.kbra.p4j.exceptions;

public class P4JServerClientException extends P4JServerException {

	private static final long serialVersionUID = -3991799135274085925L;

	public P4JServerClientException() {
	}

	public P4JServerClientException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JServerClientException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JServerClientException(final String arg0) {
		super(arg0);
	}

	public P4JServerClientException(final Throwable arg0) {
		super(arg0);
	}

}
