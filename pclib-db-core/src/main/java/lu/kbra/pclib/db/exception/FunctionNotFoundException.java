package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class FunctionNotFoundException extends DBException {

	private static final long serialVersionUID = -5174653893775224585L;

	public FunctionNotFoundException() {
	}

	public FunctionNotFoundException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public FunctionNotFoundException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public FunctionNotFoundException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public FunctionNotFoundException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public FunctionNotFoundException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public FunctionNotFoundException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public FunctionNotFoundException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FunctionNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FunctionNotFoundException(final String message) {
		super(message);
	}

	public FunctionNotFoundException(final Throwable cause) {
		super(cause);
	}

}
