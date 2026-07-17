package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class CreationFailedException extends DBException {

	public CreationFailedException() {
	}

	public CreationFailedException(String message) {
		super(message);
	}

	public CreationFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public CreationFailedException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public CreationFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CreationFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public CreationFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public CreationFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public CreationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CreationFailedException(Throwable cause) {
		super(cause);
	}

}
