package lu.kbra.p4j.exceptions;

public class P4JServerException extends P4JException {

	private static final long serialVersionUID = -8961760390672561992L;

	public P4JServerException() {
	}

	public P4JServerException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JServerException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JServerException(final String arg0) {
		super(arg0);
	}

	public P4JServerException(final Throwable arg0) {
		super(arg0);
	}

}
