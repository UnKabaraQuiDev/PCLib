package lu.kbra.pclib.parser.exception;

public class TokenizerException extends RuntimeException {

	private static final long serialVersionUID = -3994832974323522172L;

	public TokenizerException() {
	}

	public TokenizerException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TokenizerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TokenizerException(final String message) {
		super(message);
	}

	public TokenizerException(final Throwable cause) {
		super(cause);
	}

	public TokenizerException(final Throwable thr, final String message, final int line, final int column, final String value) {
		super("Exception at " + line + ":" + column + ": " + message + " (" + value + ")", thr);
	}

	public TokenizerException(final Throwable thr, final String message, final int line, final int column) {
		super("Exception at " + line + ":" + column + ": " + message, thr);
	}

	public TokenizerException(final String message, final int line, final int column) {
		super("Exception at " + line + ":" + column + ": " + message);
	}

}
