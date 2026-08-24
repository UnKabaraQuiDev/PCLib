package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoStructureException extends DBException {

	private static final long serialVersionUID = -154882552661222150L;

	public NoStructureException() {
	}

	public NoStructureException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoStructureException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoStructureException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoStructureException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoStructureException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoStructureException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoStructureException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoStructureException(final String message) {
		super(message);
	}

	public NoStructureException(final Throwable cause) {
		super(cause);
	}

}
