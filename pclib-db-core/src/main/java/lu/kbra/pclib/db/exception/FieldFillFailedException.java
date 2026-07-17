package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FieldFillFailedException extends DBException {

	public FieldFillFailedException() {
	}

	public FieldFillFailedException(String message) {
		super(message);
	}

	public FieldFillFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FieldFillFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FieldFillFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public FieldFillFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FieldFillFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FieldFillFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FieldFillFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldFillFailedException(Throwable cause) {
		super(cause);
	}

}
