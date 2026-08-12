package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoMatchingColumnException extends DBException {

	private static final long serialVersionUID = 514377140331766160L;

	public NoMatchingColumnException() {
	}

	public NoMatchingColumnException(final String message) {
		super(message);
	}

	public NoMatchingColumnException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoMatchingColumnException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingColumnException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoMatchingColumnException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoMatchingColumnException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoMatchingColumnException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoMatchingColumnException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingColumnException(final Throwable cause) {
		super(cause);
	}

}
