package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoMatchingStructureException extends DBException {

	private static final long serialVersionUID = 7624493728972807744L;

	public NoMatchingStructureException() {
	}

	public NoMatchingStructureException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NoMatchingStructureException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoMatchingStructureException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoMatchingStructureException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoMatchingStructureException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoMatchingStructureException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingStructureException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingStructureException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoMatchingStructureException(final String message) {
		super(message);
	}

	public NoMatchingStructureException(final Throwable cause) {
		super(cause);
	}

}
