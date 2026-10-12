package lu.kbra.pclib.db.migration;

public enum MigrationPhase {

	FIRST,
	REMOVE_CONSTRAINTS,
	RENAME_TABLES,
	RENAME_COLUMS,
	ADD_TABLE,
	ADD_COLUMNS,
	CHANGE_COLUMNS,
	CHANGE_CONSTRAINTS,
	ADD_CONSTRAINTS,
	REMOVE_COLUMNS,
	REMOVE_TABLES,
	MANUAL; // default for manual migrations unless they hook into another migration phase

}
