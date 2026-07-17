package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class MigrationFailedException extends DBException {

	public MigrationFailedException() {
	}

	public MigrationFailedException(String message) {
		super(message);
	}

	public MigrationFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public MigrationFailedException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public MigrationFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public MigrationFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public MigrationFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public MigrationFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public MigrationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public MigrationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MigrationFailedException(Throwable cause) {
		super(cause);
	}

}
