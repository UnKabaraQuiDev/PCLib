package lu.kbra.pclib.db.domain.db.meta;

import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

public class DefaultDatabaseHints {

	public static final String NAME_OVERRIDE = "DB_NAME_OVERRIDE";
	public static final String CHARACTER_SET = "DB_CHARACTER_SET";
	public static final String COLLATION = "DB_COLLATION";
	public static final String ENGINE = "DB_ENGINE";
	public static final String DEFINED_NAME = "DB_DEFINED_NAME";
	public static final String READ_ONLY = DefaultQueryableHints.READ_ONLY;
	public static final String TARGET_CLASS = "DB_TARGET_CLASS";

	public static final String MIGRATION_NAME = "DB_MIGRATION_NAME";
	public static final String MIGRATION_ENABLED = "DB_MIGRATION_ENABLED";

	public static final String APPLICATION_VERSION = "DB_APPLICATION_VERSION";

	private DefaultDatabaseHints() {
	}

}
