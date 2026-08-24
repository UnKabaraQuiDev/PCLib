package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class TypeClassNotFoundException extends DBException {

	private static final long serialVersionUID = 1496307050764469297L;

	public TypeClassNotFoundException() {
	}

	public TypeClassNotFoundException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public TypeClassNotFoundException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public TypeClassNotFoundException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public TypeClassNotFoundException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public TypeClassNotFoundException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public TypeClassNotFoundException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TypeClassNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TypeClassNotFoundException(final String message) {
		super(message);
	}

	public TypeClassNotFoundException(final Throwable cause) {
		super(cause);
	}

}
