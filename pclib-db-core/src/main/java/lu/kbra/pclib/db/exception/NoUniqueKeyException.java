package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoUniqueKeyException extends DBException {

	public NoUniqueKeyException() {
	}

	public NoUniqueKeyException(String message) {
		super(message);
	}

	public NoUniqueKeyException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoUniqueKeyException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NoUniqueKeyException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoUniqueKeyException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoUniqueKeyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoUniqueKeyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoUniqueKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoUniqueKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoUniqueKeyException(Throwable cause) {
		super(cause);
	}

}
