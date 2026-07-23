package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class NoGeneratedKeysException extends DBException {

	public NoGeneratedKeysException() {
	}

	public NoGeneratedKeysException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoGeneratedKeysException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoGeneratedKeysException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoGeneratedKeysException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoGeneratedKeysException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoGeneratedKeysException(String message) {
		super(message);
	}

	public NoGeneratedKeysException(Throwable cause) {
		super(cause);
	}

}
