package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class NoMatchingRowException extends DBException {

	private static final long serialVersionUID = -3124437788335351859L;

	public NoMatchingRowException() {
	}

	public NoMatchingRowException(final String message) {
		super(message);
	}

	public NoMatchingRowException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoMatchingRowException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingRowException(final Throwable cause) {
		super(cause);
	}

	public NoMatchingRowException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingRowException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

}
