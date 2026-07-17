package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class UnsupportedQueryableTypeException extends DBException {

	public UnsupportedQueryableTypeException() {
	}

	public UnsupportedQueryableTypeException(String message) {
		super(message);
	}

	public UnsupportedQueryableTypeException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public UnsupportedQueryableTypeException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public UnsupportedQueryableTypeException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public UnsupportedQueryableTypeException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public UnsupportedQueryableTypeException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public UnsupportedQueryableTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedQueryableTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnsupportedQueryableTypeException(Throwable cause) {
		super(cause);
	}

}
