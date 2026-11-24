package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class MissingFactoryException extends DBException {

	private static final long serialVersionUID = 1308052311745466441L;

	public MissingFactoryException() {
	}

	public MissingFactoryException(final String message) {
		super(message);
	}

	public MissingFactoryException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public MissingFactoryException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public MissingFactoryException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public MissingFactoryException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public MissingFactoryException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public MissingFactoryException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public MissingFactoryException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MissingFactoryException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingFactoryException(final Throwable cause) {
		super(cause);
	}

}
