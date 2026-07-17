package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class VersionConflictException extends DBException {

	public VersionConflictException() {
	}

	public VersionConflictException(String message) {
		super(message);
	}

	public VersionConflictException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public VersionConflictException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public VersionConflictException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public VersionConflictException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public VersionConflictException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public VersionConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public VersionConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public VersionConflictException(Throwable cause) {
		super(cause);
	}

}
