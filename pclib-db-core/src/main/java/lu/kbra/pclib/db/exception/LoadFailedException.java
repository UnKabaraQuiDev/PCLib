package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class LoadFailedException extends DBException {

	private static final long serialVersionUID = 4208249421626150070L;

	public LoadFailedException() {
	}

	public LoadFailedException(final String message) {
		super(message);
	}

	public LoadFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public LoadFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public LoadFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public LoadFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public LoadFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public LoadFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public LoadFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public LoadFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LoadFailedException(final Throwable cause) {
		super(cause);
	}

}
