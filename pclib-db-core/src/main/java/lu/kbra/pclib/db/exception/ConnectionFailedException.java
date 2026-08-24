package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class ConnectionFailedException extends DBException {

	private static final long serialVersionUID = 3450750697658874940L;

	public ConnectionFailedException() {
	}

	public ConnectionFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public ConnectionFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public ConnectionFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public ConnectionFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public ConnectionFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public ConnectionFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public ConnectionFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConnectionFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConnectionFailedException(final String message) {
		super(message);
	}

	public ConnectionFailedException(final Throwable cause) {
		super(cause);
	}

}
