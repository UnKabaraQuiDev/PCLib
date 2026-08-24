package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoNonNullKeyException extends DBException {

	private static final long serialVersionUID = -410390491693191882L;

	public NoNonNullKeyException() {
	}

	public NoNonNullKeyException(final String message) {
		super(message);
	}

	public NoNonNullKeyException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoNonNullKeyException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NoNonNullKeyException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoNonNullKeyException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoNonNullKeyException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoNonNullKeyException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoNonNullKeyException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoNonNullKeyException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoNonNullKeyException(final Throwable cause) {
		super(cause);
	}

}
