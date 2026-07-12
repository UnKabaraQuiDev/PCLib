package lu.kbra.pclib.db.impl;

import java.util.Collections;
import java.util.Map;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public interface SQLQueryable<T extends DatabaseEntry> {

	int count() throws DBException;

	default DatabaseConnector getConnector() {
		return this.getDatabase().getConnector();
	}

	Database getDatabase();

	DatabaseEntryUtils getDatabaseEntryUtils();

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

	default Map<String, Object> getCustomHints() {
		return Collections.emptyMap();
	}

}
