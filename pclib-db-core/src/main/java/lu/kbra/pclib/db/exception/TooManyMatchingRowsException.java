package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class TooManyMatchingRowsException extends DBException {

	public TooManyMatchingRowsException() {
	}

	public TooManyMatchingRowsException(String message) {
		super(message);
	}

	public TooManyMatchingRowsException(String message, Throwable cause) {
		super(message, cause);
	}

	public TooManyMatchingRowsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TooManyMatchingRowsException(Throwable cause) {
		super(cause);
	}

	public TooManyMatchingRowsException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public TooManyMatchingRowsException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

}
