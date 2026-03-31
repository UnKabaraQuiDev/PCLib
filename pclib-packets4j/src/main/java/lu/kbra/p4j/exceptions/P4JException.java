package lu.kbra.p4j.exceptions;

public class P4JException extends RuntimeException {

	private static final long serialVersionUID = -6793142936345900467L;

	public P4JException() {
	}

	public P4JException(final String arg0, final Throwable arg1, final boolean arg2, final boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JException(final String arg0) {
		super(arg0);
	}

	public P4JException(final Throwable arg0) {
		super(arg0);
	}

}
