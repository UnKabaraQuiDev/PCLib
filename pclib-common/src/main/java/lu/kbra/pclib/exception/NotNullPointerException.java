package lu.kbra.pclib.exception;

public class NotNullPointerException extends RuntimeException {

	private static final long serialVersionUID = -6060675695179773774L;

	public NotNullPointerException() {
	}

	public NotNullPointerException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotNullPointerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NotNullPointerException(final String message) {
		super(message);
	}

	public NotNullPointerException(final Throwable cause) {
		super(cause);
	}

}
