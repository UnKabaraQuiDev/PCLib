package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InvalidPlaceholderException extends DBException {

	private static final long serialVersionUID = 7377779913266035406L;

	public InvalidPlaceholderException() {
	}

	public InvalidPlaceholderException(final String message) {
		super(message);
	}

	public InvalidPlaceholderException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InvalidPlaceholderException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public InvalidPlaceholderException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InvalidPlaceholderException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public InvalidPlaceholderException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InvalidPlaceholderException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InvalidPlaceholderException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InvalidPlaceholderException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidPlaceholderException(final Throwable cause) {
		super(cause);
	}

}
