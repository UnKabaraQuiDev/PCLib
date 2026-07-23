package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoNameException extends DBException {

	public NoNameException() {
	}

	public NoNameException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoNameException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoNameException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoNameException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoNameException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoNameException(String message) {
		super(message);
	}

	public NoNameException(Throwable cause) {
		super(cause);
	}

}
