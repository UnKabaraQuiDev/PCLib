package lu.kbra.pclib.db.utils;

@Deprecated
public enum SQLRequestType {

	CREATE_DATABASE,
	CREATE_TABLE,
	CREATE_VIEW,

	DROP_DATABASE,
	DROP_TABLE,
	DROP_VIEW,

	TRUNCATE,

	SELECT,
	UPDATE,
	INSERT,
	DELETE;

}
