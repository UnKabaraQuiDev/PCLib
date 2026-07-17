package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoStructureException extends DBException {

	public NoStructureException() {
	}

	public NoStructureException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoStructureException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoStructureException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoStructureException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NoStructureException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoStructureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoStructureException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoStructureException(String message) {
		super(message);
	}

	public NoStructureException(Throwable cause) {
		super(cause);
	}

}
