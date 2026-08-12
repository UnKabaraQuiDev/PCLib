package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class CommitFailedException extends DBException {

	private static final long serialVersionUID = -1650701270072648901L;

	public CommitFailedException() {
	}

	public CommitFailedException(final String message) {
		super(message);
	}

	public CommitFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public CommitFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CommitFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public CommitFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public CommitFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public CommitFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CommitFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommitFailedException(final Throwable cause) {
		super(cause);
	}

}
