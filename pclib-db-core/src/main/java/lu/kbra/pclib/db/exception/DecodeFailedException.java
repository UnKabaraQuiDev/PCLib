package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DecodeFailedException extends DBException {

	private static final long serialVersionUID = 8224967636847449745L;

	public DecodeFailedException() {
	}

	public DecodeFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DecodeFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DecodeFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DecodeFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public DecodeFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DecodeFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DecodeFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DecodeFailedException(final String message) {
		super(message);
	}

	public DecodeFailedException(final Throwable cause) {
		super(cause);
	}

}
