package lu.kbra.pclib.db.impl;

import java.sql.SQLException;

import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> extends SQLNamed {

	int count() throws SQLException;

	<B> B query(SQLQuery<T, B> query) throws SQLException;

	Class<? extends SQLQueryable<T>> getTargetClass();

	DataBaseEntryUtils getDbEntryUtils();

}
