package lu.pcy113.p4j.exceptions;

public class P4JServerClientException extends P4JServerException {

	public P4JServerClientException() {
		super();
	}

	public P4JServerClientException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JServerClientException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JServerClientException(String arg0) {
		super(arg0);
	}

	public P4JServerClientException(Throwable arg0) {
		super(arg0);
	}

}
