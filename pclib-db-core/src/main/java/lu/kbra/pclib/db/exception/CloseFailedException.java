package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class CloseFailedException extends DBException {

	public CloseFailedException() {
	}

	public CloseFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public CloseFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public CloseFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public CloseFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public CloseFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CloseFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CloseFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CloseFailedException(String message) {
		super(message);
	}

	public CloseFailedException(Throwable cause) {
		super(cause);
	}

}
