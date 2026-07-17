package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InvalidReturnTypeException extends DBException {

	public InvalidReturnTypeException() {
	}

	public InvalidReturnTypeException(String message) {
		super(message);
	}

	public InvalidReturnTypeException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InvalidReturnTypeException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public InvalidReturnTypeException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InvalidReturnTypeException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public InvalidReturnTypeException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InvalidReturnTypeException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InvalidReturnTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidReturnTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidReturnTypeException(Throwable cause) {
		super(cause);
	}

}
