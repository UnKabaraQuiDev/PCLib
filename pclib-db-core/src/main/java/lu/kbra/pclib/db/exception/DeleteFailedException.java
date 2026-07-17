package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class DeleteFailedException extends DBException {

	public DeleteFailedException() {
	}

	public DeleteFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DeleteFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public DeleteFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DeleteFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DeleteFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeleteFailedException(String message) {
		super(message);
	}

	public DeleteFailedException(Throwable cause) {
		super(cause);
	}

}
