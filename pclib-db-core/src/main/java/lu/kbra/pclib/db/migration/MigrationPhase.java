package lu.kbra.pclib.db.migration;

public enum MigrationPhase {

	FIRST,
	REMOVE_CONSTRAINTS,
	ADD_TABLE,
	ADD_COLUMNS,
	RENAME_TABLE,
	CHANGE_COLUMNS,
	CHANGE_CONSTRAINTS,
	ADD_CONSTRAINTS,
	REMOVE_COLUMNS,
	REMOVE_TABLE,
	MANUAL; // default for manual migrations unless they hook into another migration phase

}
