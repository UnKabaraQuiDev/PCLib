package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class NoMatchingRowException extends DBException {

	public NoMatchingRowException() {
	}

	public NoMatchingRowException(String message) {
		super(message);
	}

	public NoMatchingRowException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMatchingRowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingRowException(Throwable cause) {
		super(cause);
	}

	public NoMatchingRowException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingRowException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

}
