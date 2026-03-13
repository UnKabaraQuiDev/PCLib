package lu.kbra.pclib.parser.exception;

public class TokenizerException extends RuntimeException {

	private static final long serialVersionUID = -3994832974323522172L;

	public TokenizerException() {
	}

	public TokenizerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TokenizerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TokenizerException(String message) {
		super(message);
	}

	public TokenizerException(Throwable cause) {
		super(cause);
	}

	public TokenizerException(Throwable thr, String message, int line, int column, String value) {
		super("Exception at " + line + ":" + column + ": " + message + " (" + value + ")", thr);
	}

	public TokenizerException(Throwable thr, String message, int line, int column) {
		super("Exception at " + line + ":" + column + ": " + message, thr);
	}

	public TokenizerException(String message, int line, int column) {
		super("Exception at " + line + ":" + column + ": " + message);
	}

}
