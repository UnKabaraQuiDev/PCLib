package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoNonNullKeyException extends DBException {

	public NoNonNullKeyException() {
	}

	public NoNonNullKeyException(String message) {
		super(message);
	}

	public NoNonNullKeyException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoNonNullKeyException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NoNonNullKeyException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoNonNullKeyException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoNonNullKeyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoNonNullKeyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoNonNullKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoNonNullKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoNonNullKeyException(Throwable cause) {
		super(cause);
	}

}
