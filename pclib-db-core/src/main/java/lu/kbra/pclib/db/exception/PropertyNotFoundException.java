package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class PropertyNotFoundException extends DBException {

	public PropertyNotFoundException() {
	}

	public PropertyNotFoundException(String message) {
		super(message);
	}

	public PropertyNotFoundException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public PropertyNotFoundException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public PropertyNotFoundException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public PropertyNotFoundException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public PropertyNotFoundException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public PropertyNotFoundException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PropertyNotFoundException(Throwable cause) {
		super(cause);
	}

}
