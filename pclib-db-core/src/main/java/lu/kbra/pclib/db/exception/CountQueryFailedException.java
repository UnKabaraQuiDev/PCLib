package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class CountQueryFailedException extends DBException {

	public CountQueryFailedException() {
	}

	public CountQueryFailedException(String message) {
		super(message);
	}

	public CountQueryFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CountQueryFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CountQueryFailedException(Throwable cause) {
		super(cause);
	}

	public CountQueryFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CountQueryFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public CountQueryFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

}
