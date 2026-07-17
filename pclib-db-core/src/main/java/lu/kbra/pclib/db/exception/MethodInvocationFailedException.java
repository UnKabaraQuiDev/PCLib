package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class MethodInvocationFailedException extends DBException {

	public MethodInvocationFailedException() {
	}

	public MethodInvocationFailedException(String message) {
		super(message);
	}

	public MethodInvocationFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public MethodInvocationFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public MethodInvocationFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public MethodInvocationFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public MethodInvocationFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public MethodInvocationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodInvocationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MethodInvocationFailedException(Throwable cause) {
		super(cause);
	}

}
