package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class UpdateFailedException extends DBException {

	public UpdateFailedException() {
	}

	public UpdateFailedException(String message) {
		super(message);
	}

	public UpdateFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UpdateFailedException(Throwable cause) {
		super(cause);
	}

	public UpdateFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public UpdateFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

}
