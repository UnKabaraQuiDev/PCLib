package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class NoGeneratedKeysException extends DBException {

	private static final long serialVersionUID = -3953131555421913875L;

	public NoGeneratedKeysException() {
	}

	public NoGeneratedKeysException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoGeneratedKeysException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoGeneratedKeysException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoGeneratedKeysException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoGeneratedKeysException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoGeneratedKeysException(final String message) {
		super(message);
	}

	public NoGeneratedKeysException(final Throwable cause) {
		super(cause);
	}

}
