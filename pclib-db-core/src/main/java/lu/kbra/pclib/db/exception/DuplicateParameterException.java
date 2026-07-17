package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DuplicateParameterException extends DBException {

	public DuplicateParameterException() {
	}

	public DuplicateParameterException(String message) {
		super(message);
	}

	public DuplicateParameterException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DuplicateParameterException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public DuplicateParameterException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DuplicateParameterException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public DuplicateParameterException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DuplicateParameterException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DuplicateParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DuplicateParameterException(Throwable cause) {
		super(cause);
	}

}
