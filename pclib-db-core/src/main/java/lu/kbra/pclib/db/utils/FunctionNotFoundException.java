package lu.kbra.pclib.db.utils;

public class FunctionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7026391811526164674L;

	public FunctionNotFoundException() {
	}

	public FunctionNotFoundException(final String message) {
		super(message);
	}

	public FunctionNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FunctionNotFoundException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FunctionNotFoundException(final Throwable cause) {
		super(cause);
	}

}
