package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.table.DBStructure;

public class DBException extends RuntimeException {

	public static final String INCLUDE_STRUCTURE_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName()
			+ ".include_structure_in_exception";
	public static boolean INCLUDE_STRUCTURE_IN_EXCEPTION = PCUtils.getBoolean(INCLUDE_STRUCTURE_IN_EXCEPTION_PROPERTY, true);

	public static final String INCLUDE_SQL_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName() + ".include_sql_in_exception";
	public static boolean INCLUDE_SQL_IN_EXCEPTION = PCUtils.getBoolean(INCLUDE_SQL_IN_EXCEPTION_PROPERTY, true);

	private static final long serialVersionUID = -685673716198900827L;

	public DBException() {
	}

	public DBException(final String message) {
		super(message);
	}

	public DBException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DBException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DBException(final Throwable cause) {
		super(cause);
	}

	public DBException(String message, String sql, DBStructure structure, Throwable e) {
		super(message + (INCLUDE_SQL_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : "")
				+ (INCLUDE_STRUCTURE_IN_EXCEPTION
						? "\n --- Structure ---\n" + (structure == null ? "<none>" : PCUtils.printTree(structure.toMap()))
						: ""),
				e);
	}

}
