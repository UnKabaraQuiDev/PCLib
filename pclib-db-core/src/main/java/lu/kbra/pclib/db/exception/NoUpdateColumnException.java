package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoUpdateColumnException extends DBException {

	private static final long serialVersionUID = 9063209093919322022L;

	public NoUpdateColumnException() {
	}

	public NoUpdateColumnException(final String message) {
		super(message);
	}

	public NoUpdateColumnException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoUpdateColumnException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NoUpdateColumnException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoUpdateColumnException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoUpdateColumnException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoUpdateColumnException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoUpdateColumnException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoUpdateColumnException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoUpdateColumnException(final Throwable cause) {
		super(cause);
	}

}
