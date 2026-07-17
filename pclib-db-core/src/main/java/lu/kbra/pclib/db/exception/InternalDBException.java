package lu.kbra.pclib.db.exception;

import java.sql.SQLException;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InternalDBException extends DBException {

	public InternalDBException() {
	}

	public InternalDBException(AbstractDBStructure structure, SQLException e) {
		this("", null, structure, null, e);
	}

	public InternalDBException(String message) {
		super(message);
	}

	public InternalDBException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message + (DBException.INCLUDE_SQL_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : "")
				+ (DBException.INCLUDE_STRUCTURE_IN_EXCEPTION
						? "\n --- Structure ---\n" + (structure == null ? "<none>" : structure.toTreeString())
						: "")
				+ (DBException.INCLUDE_QUERY_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : ""), e);
	}

	public InternalDBException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public InternalDBException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalDBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InternalDBException(Throwable cause) {
		super(cause);
	}

}
