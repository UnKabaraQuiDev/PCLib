package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class MissingDependencyException extends DBException {

	public MissingDependencyException() {
	}

	public MissingDependencyException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public MissingDependencyException(AbstractDBStructure structure) {
		super(structure);
	}

	public MissingDependencyException(String message) {
		super(message);
	}

	public MissingDependencyException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public MissingDependencyException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public MissingDependencyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public MissingDependencyException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public MissingDependencyException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public MissingDependencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingDependencyException(Throwable cause) {
		super(cause);
	}

}
