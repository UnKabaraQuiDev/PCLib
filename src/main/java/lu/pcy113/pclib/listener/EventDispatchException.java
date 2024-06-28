package lu.pcy113.pclib.listener;

@SuppressWarnings("serial")
public class EventDispatchException extends RuntimeException {

	public EventDispatchException() {
		super();
	}

	public EventDispatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EventDispatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public EventDispatchException(String message) {
		super(message);
	}

	public EventDispatchException(Throwable cause) {
		super(cause);
	}

}
