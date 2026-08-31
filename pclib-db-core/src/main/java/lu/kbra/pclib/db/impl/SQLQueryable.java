package lu.kbra.pclib.db.impl;

import java.sql.Statement;
import java.util.Collections;
import java.util.Map;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public interface SQLQueryable<T extends DatabaseEntry> {

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

	default @Qualified String getQualifiedName() {
		return this.getStructure().getQualifiedName();
	}

	SQLQueryableStructure getStructure();

	default Class<? extends SQLQueryable<T>> getTargetClass() {
		return (Class<? extends SQLQueryable<T>>) this.getStructure().getTargetClass();
	}

	default Map<String, Object> getCustomHints() {
		return Collections.emptyMap();
	}

	SQLQueryableHookManager getQueryableHookManager();

	int count() throws DBException;

	<B> B query(SQLQuery<T, B> query) throws DBException;

	default String getStatementAsSQL(final Statement stmt) {
		if (stmt == null) {
			return "null";
		}
		return this.getDatabaseEntryUtils().getStructureVisitor().statementToString(stmt);
	}

}
