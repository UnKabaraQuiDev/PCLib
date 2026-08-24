package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class MethodInvocationFailedException extends DBException {

	private static final long serialVersionUID = -1606221590744321928L;

	public MethodInvocationFailedException() {
	}

	public MethodInvocationFailedException(final String message) {
		super(message);
	}

	public MethodInvocationFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public MethodInvocationFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public MethodInvocationFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public MethodInvocationFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public MethodInvocationFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public MethodInvocationFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MethodInvocationFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MethodInvocationFailedException(final Throwable cause) {
		super(cause);
	}

}
