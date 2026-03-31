package lu.kbra.pclib.db.exception;

public class DBException extends RuntimeException {

	private static final long serialVersionUID = -685673716198900827L;

	public DBException() {
	}

	public DBException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DBException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DBException(final String message) {
		super(message);
	}

	public DBException(final Throwable cause) {
		super(cause);
	}

}
