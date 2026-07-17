package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class ConnectionAlreadyClosedException extends DBException {

	public ConnectionAlreadyClosedException() {
	}

	public ConnectionAlreadyClosedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public ConnectionAlreadyClosedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public ConnectionAlreadyClosedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public ConnectionAlreadyClosedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public ConnectionAlreadyClosedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public ConnectionAlreadyClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConnectionAlreadyClosedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionAlreadyClosedException(String message) {
		super(message);
	}

	public ConnectionAlreadyClosedException(Throwable cause) {
		super(cause);
	}

}
