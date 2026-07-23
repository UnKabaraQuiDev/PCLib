package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoPrimaryKeyException extends DBException {

	public NoPrimaryKeyException() {
	}

	public NoPrimaryKeyException(String message) {
		super(message);
	}

	public NoPrimaryKeyException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoPrimaryKeyException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NoPrimaryKeyException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoPrimaryKeyException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoPrimaryKeyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoPrimaryKeyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoPrimaryKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPrimaryKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoPrimaryKeyException(Throwable cause) {
		super(cause);
	}

}
