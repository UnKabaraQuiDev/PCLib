package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InternalDBException extends DBException {

	public InternalDBException() {
	}

	public InternalDBException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public InternalDBException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InternalDBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InternalDBException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalDBException(String message) {
		super(message);
	}

	public InternalDBException(Throwable cause) {
		super(cause);
	}

}
