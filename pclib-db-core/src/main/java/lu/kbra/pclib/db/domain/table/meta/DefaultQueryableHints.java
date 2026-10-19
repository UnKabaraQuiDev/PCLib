package lu.kbra.pclib.db.domain.table.meta;

import lu.kbra.pclib.db.domain.db.meta.DefaultDatabaseHints;

public class DefaultQueryableHints {

	public static final String NAME_OVERRIDE = "QA_NAME_OVERRIDE";
	public static final String CHARACTER_SET = DefaultDatabaseHints.CHARACTER_SET;
	public static final String COLLATION = DefaultDatabaseHints.COLLATION;
	public static final String ENGINE = DefaultDatabaseHints.ENGINE;
	public static final String DEFINED_NAME = "QA_DEFINED_NAME";
	public static final String TARGET_CLASS = "QA_TARGET_CLASS";
	public static final String ENTRY_CLASS = "QA_ENTRY_CLASS";
	public static final String SCHEMA = "QA_SCHEMA";
	public static final String READ_ONLY = "QA_READ_ONLY";

	public static final String INTERNAL = "QA_INTERNAL";
	public static final String SYNTHETIC = "QA_SYNTHETIC";
	public static final String TABLE_ID = "QA_TABLE_ID";
	public static final String SKIP_LINKS_SCAN = "QA_SKIP_LINKS_SCAN";
	public static final String MANUAL = "QA_MANUAL";

	// View
	public static final String VIEW_NAME = "QA_VIEW_NAME";
	public static final String VIEW_CONDITION = "QA_VIEW_CONDITION";
	public static final String VIEW_CUSTOM_SQL = "QA_VIEW_CUSTOM_SQL";
	public static final String VIEW_GROUP_BY = "QA_VIEW_GROUP_BY";
	public static final String VIEW_AS_NAME = "QA_VIEW_AS_NAME";
	public static final String VIEW_DISTINCT = "QA_VIEW_DISTINCT";
	public static final String VIEW_TYPE = "QA_VIEW_TYPE";

	public static final String VIEW_JOIN_TYPE = "QA_VIEW_JOIN_TYPE";
	public static final String VIEW_JOIN_ON_CONDITION = "QA_VIEW_JOIN_ON_CONDITION";

	public static final String VIEW_TABLES = "QA_VIEW_TABLES";
	public static final String VIEW_TABLE = "QA_VIEW_TABLE";

	public static final String VIEW_UNION_TABLES = "QA_VIEW_UNION_TABLES";
	public static final String VIEW_UNION_TABLE = "QA_VIEW_UNION_TABLE";

	public static final String VIEW_WITH_TABLES = "QA_VIEW_WITH_TABLES";
	public static final String VIEW_WITH_TABLE = "QA_VIEW_WITH_TABLE";

	public static final String VIEW_COLUMNS = "QA_VIEW_COLUMNS";
	public static final String VIEW_COLUMN = "QA_VIEW_COLUMN";
	public static final String VIEW_COLUMN_AS_NAME = "QA_VIEW_COLUMN_AS_NAME";
	public static final String VIEW_COLUMN_NAME = "QA_VIEW_COLUMN_NAME";
	public static final String VIEW_COLUMN_FUNCTION = "QA_VIEW_COLUMN_FUNCTION";

	public static final String VIEW_ORDER_BY = "QA_VIEW_ORDER_BY";
	public static final String VIEW_ORDER_BY_EXPRESSION = "QA_VIEW_ORDER_BY_EXPRESSION";
	public static final String VIEW_ORDER_BY_DIR = "QA_VIEW_ORDER_BY_DIR";

	private DefaultQueryableHints() {
	}

}
