package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoMatchingColumnException extends DBException {

	public NoMatchingColumnException() {
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public NoMatchingColumnException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
