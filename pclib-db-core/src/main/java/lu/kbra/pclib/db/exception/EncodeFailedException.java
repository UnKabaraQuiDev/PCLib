package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class EncodeFailedException extends DBException {

	public EncodeFailedException() {
	}

	public EncodeFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public EncodeFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public EncodeFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public EncodeFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public EncodeFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public EncodeFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EncodeFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public EncodeFailedException(String message) {
		super(message);
	}

	public EncodeFailedException(Throwable cause) {
		super(cause);
	}

}
