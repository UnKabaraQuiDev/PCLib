package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class CommitFailedException extends DBException {

	public CommitFailedException() {
	}

	public CommitFailedException(String message) {
		super(message);
	}

	public CommitFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public CommitFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public CommitFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public CommitFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public CommitFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public CommitFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommitFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommitFailedException(Throwable cause) {
		super(cause);
	}

}
