package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class TypeClassNotFoundException extends DBException {

	public TypeClassNotFoundException() {
	}

	public TypeClassNotFoundException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public TypeClassNotFoundException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public TypeClassNotFoundException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public TypeClassNotFoundException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public TypeClassNotFoundException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public TypeClassNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TypeClassNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TypeClassNotFoundException(String message) {
		super(message);
	}

	public TypeClassNotFoundException(Throwable cause) {
		super(cause);
	}

}
