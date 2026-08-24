package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FieldStoreFailedException extends DataAccessException {

	private static final long serialVersionUID = -5453641510206093662L;

	public FieldStoreFailedException() {
	}

	public FieldStoreFailedException(final String message) {
		super(message);
	}

	public FieldStoreFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FieldStoreFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FieldStoreFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public FieldStoreFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FieldStoreFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FieldStoreFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FieldStoreFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FieldStoreFailedException(final Throwable cause) {
		super(cause);
	}

}
