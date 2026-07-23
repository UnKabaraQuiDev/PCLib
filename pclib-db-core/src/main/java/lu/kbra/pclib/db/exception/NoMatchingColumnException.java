package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoMatchingColumnException extends DBException {

	public NoMatchingColumnException() {
	}

	public NoMatchingColumnException(String message) {
		super(message);
	}

	public NoMatchingColumnException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoMatchingColumnException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMatchingColumnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingColumnException(Throwable cause) {
		super(cause);
	}

}
