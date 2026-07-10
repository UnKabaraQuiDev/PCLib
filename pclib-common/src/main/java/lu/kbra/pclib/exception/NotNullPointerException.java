package lu.kbra.pclib.exception;

public class NotNullPointerException extends RuntimeException {

	private static final long serialVersionUID = -6060675695179773774L;

	public NotNullPointerException() {
	}

	public NotNullPointerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotNullPointerException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotNullPointerException(String message) {
		super(message);
	}

	public NotNullPointerException(Throwable cause) {
		super(cause);
	}

}
