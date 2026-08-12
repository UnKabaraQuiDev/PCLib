package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class UnsupportedQueryTypeException extends DBException {

	private static final long serialVersionUID = -2740108010830936709L;

	public UnsupportedQueryTypeException() {
	}

	public UnsupportedQueryTypeException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public UnsupportedQueryTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public UnsupportedQueryTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public UnsupportedQueryTypeException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public UnsupportedQueryTypeException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public UnsupportedQueryTypeException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnsupportedQueryTypeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedQueryTypeException(final String message) {
		super(message);
	}

	public UnsupportedQueryTypeException(final Throwable cause) {
		super(cause);
	}

}
