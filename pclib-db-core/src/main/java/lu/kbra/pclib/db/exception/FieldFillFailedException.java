package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FieldFillFailedException extends DBException {

	private static final long serialVersionUID = -5453641510206093662L;

	public FieldFillFailedException() {
	}

	public FieldFillFailedException(final String message) {
		super(message);
	}

	public FieldFillFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FieldFillFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FieldFillFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public FieldFillFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FieldFillFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FieldFillFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FieldFillFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldFillFailedException(final Throwable cause) {
		super(cause);
	}

}
