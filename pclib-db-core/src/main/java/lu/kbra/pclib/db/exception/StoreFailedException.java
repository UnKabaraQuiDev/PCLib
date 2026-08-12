package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class StoreFailedException extends DBException {

	private static final long serialVersionUID = -444230077460704936L;

	public StoreFailedException() {
	}

	public StoreFailedException(final String message) {
		super(message);
	}

	public StoreFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public StoreFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public StoreFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public StoreFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public StoreFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public StoreFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public StoreFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public StoreFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StoreFailedException(final Throwable cause) {
		super(cause);
	}

}
