package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.domain.table.DBStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> {

	int count() throws DBException;

	default DataBaseConnector getConnector() {
		return getDatabase().getConnector();
	}

	DataBase getDatabase();

	DataBaseEntryUtils getDataBaseEntryUtils();

	default Class<T> getEntryClass() {
		return (Class<T>) this.getStructure().getEntryClass();
	}

	default String getName() {
		return getStructure().getName();
	}

	default String getQualifiedName() {
		return getStructure().getQualifiedName();
	}

	DBStructure getStructure();

	default Class<? extends SQLQueryable<T>> getTargetClass() {
		return (Class<? extends SQLQueryable<T>>) getStructure().getTargetClass();
	}

	<B> B query(SQLQuery<T, B> query) throws DBException;

}
