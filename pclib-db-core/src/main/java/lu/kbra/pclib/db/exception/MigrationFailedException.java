package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class MigrationFailedException extends DBException {

	private static final long serialVersionUID = -6142225127411108308L;

	public MigrationFailedException() {
	}

	public MigrationFailedException(final String message) {
		super(message);
	}

	public MigrationFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public MigrationFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public MigrationFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public MigrationFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public MigrationFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public MigrationFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public MigrationFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MigrationFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MigrationFailedException(final Throwable cause) {
		super(cause);
	}

}
