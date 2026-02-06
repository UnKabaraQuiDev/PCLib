package lu.pcy113.p4j.exceptions;

public class P4JClientServerException extends P4JClientException {

	public P4JClientServerException() {
		super();
	}

	public P4JClientServerException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public P4JClientServerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public P4JClientServerException(String arg0) {
		super(arg0);
	}

	public P4JClientServerException(Throwable arg0) {
		super(arg0);
	}

}
