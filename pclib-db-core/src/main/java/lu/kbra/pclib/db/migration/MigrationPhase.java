package lu.kbra.pclib.db.migration;

public enum MigrationPhase {

	FIRST,
	REMOVE_CONSTRAINTS,
	RENAME_TABLE,
	ADD_TABLE,
	ADD_COLUMNS,
	CHANGE_COLUMNS,
	CHANGE_CONSTRAINTS,
	ADD_CONSTRAINTS,
	REMOVE_COLUMNS,
	REMOVE_TABLE,
	MANUAL; // default for manual migrations unless they hook into another migration phase

}
