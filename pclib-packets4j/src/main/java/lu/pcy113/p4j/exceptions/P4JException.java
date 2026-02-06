package lu.pcy113.p4j.exceptions;

public class P4JException extends RuntimeException {

	public P4JException() {
		super();
	}

	public P4JException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JException(String arg0) {
		super(arg0);
	}

	public P4JException(Throwable arg0) {
		super(arg0);
	}

}
