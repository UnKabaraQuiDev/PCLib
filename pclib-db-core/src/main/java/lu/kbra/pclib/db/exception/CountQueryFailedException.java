package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class CountQueryFailedException extends DBException {

	private static final long serialVersionUID = 2585806999183054419L;

	public CountQueryFailedException() {
	}

	public CountQueryFailedException(final String message) {
		super(message);
	}

	public CountQueryFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CountQueryFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CountQueryFailedException(final Throwable cause) {
		super(cause);
	}

	public CountQueryFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CountQueryFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public CountQueryFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

}
