package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FieldReadFailedException extends DataAccessException {

	private static final long serialVersionUID = -2801617627024014946L;

	public FieldReadFailedException() {
	}

	public FieldReadFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public FieldReadFailedException(final String message) {
		super(message);
	}

	public FieldReadFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FieldReadFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FieldReadFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FieldReadFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FieldReadFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public FieldReadFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FieldReadFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldReadFailedException(final Throwable cause) {
		super(cause);
	}

}
