package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class PingFailedException extends DBException {

	public PingFailedException() {
	}

	public PingFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public PingFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public PingFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public PingFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public PingFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public PingFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PingFailedException(String message) {
		super(message);
	}

	public PingFailedException(Throwable cause) {
		super(cause);
	}

}
