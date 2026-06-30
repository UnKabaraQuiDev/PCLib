package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> {

	int count() throws DBException;

	default DataBaseConnector getConnector() {
		return getDatabase().getConnector();
	}

	DataBaseEntryUtils getDataBaseEntryUtils();

	default String getName() {
		return getDataBaseEntryUtils().getQueryableName(getTargetClass());
	}

	default String getQualifiedName() {
		return getDataBaseEntryUtils().getStructureVisitor().qualifiedName(this);
	}

	Class<? extends SQLQueryable<T>> getTargetClass();

	Class<? extends T> getEntryClass();

	<B> B query(SQLQuery<T, B> query) throws DBException;

	DataBase getDatabase();

}
