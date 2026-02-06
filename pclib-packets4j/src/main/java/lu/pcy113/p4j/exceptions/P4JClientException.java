package lu.pcy113.p4j.exceptions;

public class P4JClientException extends P4JException {

	public P4JClientException() {
		super();
	}

	public P4JClientException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JClientException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JClientException(String arg0) {
		super(arg0);
	}

	public P4JClientException(Throwable arg0) {
		super(arg0);
	}

}
