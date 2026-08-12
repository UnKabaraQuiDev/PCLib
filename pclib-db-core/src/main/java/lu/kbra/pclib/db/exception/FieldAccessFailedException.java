package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FieldAccessFailedException extends DBException {

	private static final long serialVersionUID = 7277170568904356803L;

	public FieldAccessFailedException() {
	}

	public FieldAccessFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public FieldAccessFailedException(final String message) {
		super(message);
	}

	public FieldAccessFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FieldAccessFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FieldAccessFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FieldAccessFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FieldAccessFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public FieldAccessFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FieldAccessFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldAccessFailedException(final Throwable cause) {
		super(cause);
	}

}
