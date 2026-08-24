package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InternalDBException extends DBException {

	private static final long serialVersionUID = 201108105361309279L;

	public InternalDBException() {
	}

	public InternalDBException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public InternalDBException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InternalDBException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InternalDBException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InternalDBException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public InternalDBException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InternalDBException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InternalDBException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InternalDBException(final String message) {
		super(message);
	}

	public InternalDBException(final Throwable cause) {
		super(cause);
	}

}
