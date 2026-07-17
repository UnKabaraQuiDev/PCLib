package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoUpdateColumnException extends DBException {

	public NoUpdateColumnException() {
	}

	public NoUpdateColumnException(String message) {
		super(message);
	}

	public NoUpdateColumnException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoUpdateColumnException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NoUpdateColumnException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoUpdateColumnException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoUpdateColumnException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoUpdateColumnException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoUpdateColumnException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoUpdateColumnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoUpdateColumnException(Throwable cause) {
		super(cause);
	}

}
