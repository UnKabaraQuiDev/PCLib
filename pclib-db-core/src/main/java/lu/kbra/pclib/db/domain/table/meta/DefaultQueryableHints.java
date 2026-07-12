package lu.kbra.pclib.db.domain.table.meta;

public class DefaultQueryableHints {

	public static final String NAME_OVERRIDE = "NAME_OVERRIDE";
	public static final String CHARACTER_SET = "CHARACTER_SET";
	public static final String COLLATION = "COLLATION";
	public static final String ENGINE = "ENGINE";
	public static final String DEFINED_NAME = "DEFINED_NAME";
	public static final String TARGET_CLASS = "TARGET_CLASS";

	// DB_View
	public static final String VIEW_NAME = "VIEW_NAME";
	public static final String VIEW_CONDITION = "VIEW_CONDITION";
	public static final String VIEW_CUSTOM_SQL = "VIEW_CUSTOM_SQL";
	public static final String VIEW_GROUP_BY = "VIEW_GROUP_BY";
	public static final String VIEW_AS_NAME = "VIEW_AS_NAME";
	public static final String VIEW_DISTINCT = "VIEW_DISTINCT";
	public static final String VIEW_TYPE = "VIEW_TYPE";

	public static final String VIEW_JOIN_TYPE = "VIEW_JOIN_TYPE";
	public static final String VIEW_JOIN_ON_CONDITION = "VIEW_JOIN_ON_CONDITION";

	public static final String VIEW_TABLES = "VIEW_TABLES";
	public static final String VIEW_TABLE = "VIEW_TABLE";

	public static final String VIEW_UNION_TABLES = "VIEW_UNION_TABLES";
	public static final String VIEW_UNION_TABLE = "VIEW_UNION_TABLE";

	public static final String VIEW_WITH_TABLES = "VIEW_WITH_TABLES";
	public static final String VIEW_WITH_TABLE = "VIEW_WITH_TABLE";

	public static final String VIEW_COLUMNS = "VIEW_COLUMNS";
	public static final String VIEW_COLUMN = "VIEW_COLUMN";
	public static final String VIEW_COLUMN_AS_NAME = "VIEW_COLUMN_AS_NAME";
	public static final String VIEW_COLUMN_NAME = "VIEW_COLUMN_NAME";
	public static final String VIEW_COLUMN_FUNCTION = "VIEW_COLUMN_FUNCTION";

	public static final String VIEW_ORDER_BY = "VIEW_ORDER_BY";
	public static final String VIEW_ORDER_BY_COLUMN = "VIEW_ORDER_BY_COLUMN";
	public static final String VIEW_ORDER_BY_DIR = "VIEW_ORDER_BY_DIR";

	protected DefaultQueryableHints() {
	}

}
