package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DecodeFailedException extends DBException {

	public DecodeFailedException() {
	}

	public DecodeFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DecodeFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DecodeFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DecodeFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public DecodeFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DecodeFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DecodeFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecodeFailedException(String message) {
		super(message);
	}

	public DecodeFailedException(Throwable cause) {
		super(cause);
	}

}
