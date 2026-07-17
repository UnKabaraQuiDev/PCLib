package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FieldAccessFailedException extends DBException {

	public FieldAccessFailedException() {
	}

	public FieldAccessFailedException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public FieldAccessFailedException(String message) {
		super(message);
	}

	public FieldAccessFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FieldAccessFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FieldAccessFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FieldAccessFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FieldAccessFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public FieldAccessFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldAccessFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldAccessFailedException(Throwable cause) {
		super(cause);
	}

}
