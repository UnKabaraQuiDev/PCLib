package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> {

	int count() throws DBException;

	default DataBaseConnector getConnector() {
		return this.getDatabase().getConnector();
	}

	DataBase getDatabase();

	DataBaseEntryUtils getDataBaseEntryUtils();

	default Class<T> getEntryClass() {
		return (Class<T>) this.getStructure().getEntryClass();
	}

	default String getName() {
		return this.getStructure().getName();
	}

	default String getQualifiedName() {
		return this.getStructure().getQualifiedName();
	}

	SQLQueryableStructure getStructure();

	default Class<? extends SQLQueryable<T>> getTargetClass() {
		return (Class<? extends SQLQueryable<T>>) this.getStructure().getTargetClass();
	}

	<B> B query(SQLQuery<T, B> query) throws DBException;

}
