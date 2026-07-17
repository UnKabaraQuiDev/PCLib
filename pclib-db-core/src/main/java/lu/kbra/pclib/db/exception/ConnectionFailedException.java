package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class ConnectionFailedException extends DBException {

	public ConnectionFailedException() {
	}

	public ConnectionFailedException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public ConnectionFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public ConnectionFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public ConnectionFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public ConnectionFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public ConnectionFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public ConnectionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConnectionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionFailedException(String message) {
		super(message);
	}

	public ConnectionFailedException(Throwable cause) {
		super(cause);
	}

}
