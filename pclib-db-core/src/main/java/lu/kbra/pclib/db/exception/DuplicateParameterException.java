package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DuplicateParameterException extends DBException {

	private static final long serialVersionUID = 1308052311745466441L;

	public DuplicateParameterException() {
	}

	public DuplicateParameterException(final String message) {
		super(message);
	}

	public DuplicateParameterException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DuplicateParameterException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public DuplicateParameterException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DuplicateParameterException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public DuplicateParameterException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DuplicateParameterException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DuplicateParameterException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DuplicateParameterException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DuplicateParameterException(final Throwable cause) {
		super(cause);
	}

}
