package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InvalidColumnTypeException extends DBException {

	public InvalidColumnTypeException() {
	}

	public InvalidColumnTypeException(String message) {
		super(message);
	}

	public InvalidColumnTypeException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InvalidColumnTypeException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public InvalidColumnTypeException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InvalidColumnTypeException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public InvalidColumnTypeException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InvalidColumnTypeException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InvalidColumnTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidColumnTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidColumnTypeException(Throwable cause) {
		super(cause);
	}

}
