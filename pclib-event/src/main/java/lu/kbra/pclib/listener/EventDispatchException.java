package lu.kbra.pclib.listener;

public class EventDispatchException extends RuntimeException {

	private static final long serialVersionUID = -7210640392216701971L;

	public EventDispatchException() {
	}

	public EventDispatchException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EventDispatchException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EventDispatchException(final String message) {
		super(message);
	}

	public EventDispatchException(final Throwable cause) {
		super(cause);
	}

}
