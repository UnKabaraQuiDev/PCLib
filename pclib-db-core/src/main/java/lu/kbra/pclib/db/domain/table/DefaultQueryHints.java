package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

public class DefaultQueryHints {

	public static final String CONDITION = "CONDITION";
	public static final String CUSTOM_SQL = "CUSTOM_SQL";
	public static final String GROUP_BY = "GROUP_BY";
	public static final String DISTINCT = "DISTINCT";
	public static final String STRATEGY = "STRATEGY";
	public static final String METHOD_NAME = "METHOD_NAME";

	public static final String PARAMETERS = "PARAMETERS";
	public static final String PARAM_PARAM = "PARAM_PARAM";
	public static final String PARAM_INDEX = "PARAM_INDEX";
	public static final String PARAM_COMPARATOR = "PARAM_COMPARATOR";
	public static final String PARAM_IGNORE_NULL = "PARAM_IGNORE_NULL";
	public static final String PARAM_NAME = "PARAM_NAME";
	public static final String PARAM_MEMBER_NAME = "PARAM_MEMBER_NAME";
	public static final String PARAM_LIMIT = "PARAM_LIMIT";
	public static final String PARAM_OFFSET = "PARAM_OFFSET";

	public static final String COLUMNS = "COLUMNS";
	public static final String RETURN_COLUMNS = "RETURN_COLUMNS";

	public static final String JOIN_TYPE = DefaultQueryableHints.VIEW_JOIN_TYPE;
	public static final String ON_CONDITION = DefaultQueryableHints.VIEW_JOIN_ON_CONDITION;

	public static final String TABLES = DefaultQueryableHints.VIEW_TABLES;
	public static final String TABLE = DefaultQueryableHints.VIEW_TABLE;
	public static final String NAME = DefaultQueryableHints.VIEW_NAME;
	public static final String TYPE = DefaultQueryableHints.VIEW_TYPE;

	public static final String AS_NAME = DefaultQueryableHints.VIEW_AS_NAME;
	public static final String JOIN_COLUMN = DefaultQueryableHints.VIEW_COLUMN;
	public static final String JOIN_COLUMN_AS_NAME = DefaultQueryableHints.VIEW_COLUMN_AS_NAME;
	public static final String JOIN_COLUMN_NAME = DefaultQueryableHints.VIEW_COLUMN_NAME;
	public static final String JOIN_COLUMN_FUNCTION = DefaultQueryableHints.VIEW_COLUMN_FUNCTION;

	public static final String ORDER_BY = DefaultQueryableHints.VIEW_ORDER_BY;
	public static final String ORDER_BY_EXPRESSION = DefaultQueryableHints.VIEW_ORDER_BY_EXPRESSION;
	public static final String ORDER_BY_DIR = DefaultQueryableHints.VIEW_ORDER_BY_DIR;

	private DefaultQueryHints() {
	}

}
