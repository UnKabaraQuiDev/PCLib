package lu.kbra.pclib.db.exception;

import lombok.Getter;
import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class InternalSQLException extends DBException {


	public InternalSQLException() {
	}

	public InternalSQLException(String message) {
		super(message);
	}

	public InternalSQLException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InternalSQLException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InternalSQLException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public InternalSQLException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InternalSQLException(Throwable cause) {
		super(cause);
	}

	public InternalSQLException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message + (DBException.INCLUDE_SQL_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : "")
				+ (DBException.INCLUDE_STRUCTURE_IN_EXCEPTION
						? "\n --- Structure ---\n" + (structure == null ? "<none>" : structure.toTreeString())
						: "")
				+ (DBException.INCLUDE_QUERY_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : ""), e);
	}

}
