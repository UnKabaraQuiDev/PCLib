package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoMatchingFieldException extends DBException {

	public NoMatchingFieldException() {
	}

	public NoMatchingFieldException(String message) {
		super(message);
	}

	public NoMatchingFieldException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoMatchingFieldException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NoMatchingFieldException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingFieldException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoMatchingFieldException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoMatchingFieldException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoMatchingFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMatchingFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingFieldException(Throwable cause) {
		super(cause);
	}

}
