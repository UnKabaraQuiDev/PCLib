package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class ScanFailedException extends DBException {

	private static final long serialVersionUID = 2450650516172694848L;

	public ScanFailedException() {
	}

	public ScanFailedException(final String message) {
		super(message);
	}

	public ScanFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public ScanFailedException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public ScanFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public ScanFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public ScanFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public ScanFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public ScanFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ScanFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScanFailedException(final Throwable cause) {
		super(cause);
	}

}
