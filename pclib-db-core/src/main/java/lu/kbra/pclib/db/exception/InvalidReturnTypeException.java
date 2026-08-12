package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InvalidReturnTypeException extends DBException {

	private static final long serialVersionUID = -6592066620809328668L;

	public InvalidReturnTypeException() {
	}

	public InvalidReturnTypeException(final String message) {
		super(message);
	}

	public InvalidReturnTypeException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InvalidReturnTypeException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public InvalidReturnTypeException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InvalidReturnTypeException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public InvalidReturnTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InvalidReturnTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InvalidReturnTypeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InvalidReturnTypeException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidReturnTypeException(final Throwable cause) {
		super(cause);
	}

}
