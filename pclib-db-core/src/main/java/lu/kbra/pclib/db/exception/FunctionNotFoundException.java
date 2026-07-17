package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FunctionNotFoundException extends DBException {

	public FunctionNotFoundException() {
	}

	public FunctionNotFoundException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public FunctionNotFoundException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FunctionNotFoundException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FunctionNotFoundException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FunctionNotFoundException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public FunctionNotFoundException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FunctionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FunctionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public FunctionNotFoundException(String message) {
		super(message);
	}

	public FunctionNotFoundException(Throwable cause) {
		super(cause);
	}

}
