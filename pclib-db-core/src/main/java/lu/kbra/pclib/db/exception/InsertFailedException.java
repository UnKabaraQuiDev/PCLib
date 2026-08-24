package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;

public class InsertFailedException extends DBException {

	private static final long serialVersionUID = 234872492525936602L;

	public InsertFailedException() {
	}

	public InsertFailedException(final String message) {
		super(message);
	}

	public InsertFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InsertFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InsertFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public InsertFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InsertFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InsertFailedException(final Throwable cause) {
		super(cause);
	}

}
