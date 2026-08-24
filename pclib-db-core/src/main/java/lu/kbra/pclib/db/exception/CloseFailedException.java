package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class CloseFailedException extends DBException {

	private static final long serialVersionUID = -8048743516775779620L;

	public CloseFailedException() {
	}

	public CloseFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public CloseFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public CloseFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public CloseFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public CloseFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CloseFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CloseFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CloseFailedException(final String message) {
		super(message);
	}

	public CloseFailedException(final Throwable cause) {
		super(cause);
	}

}
