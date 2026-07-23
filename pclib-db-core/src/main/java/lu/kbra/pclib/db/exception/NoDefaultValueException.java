package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoDefaultValueException extends DBException {

	public NoDefaultValueException() {
	}

	public NoDefaultValueException(String message) {
		super(message);
	}

	public NoDefaultValueException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoDefaultValueException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NoDefaultValueException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoDefaultValueException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoDefaultValueException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoDefaultValueException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoDefaultValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoDefaultValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoDefaultValueException(Throwable cause) {
		super(cause);
	}

}
