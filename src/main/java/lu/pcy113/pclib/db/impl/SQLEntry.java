package lu.pcy113.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import lu.pcy113.pclib.db.DataBaseTable;

public interface SQLEntry extends Cloneable {

	SQLEntry clone();

	public interface SafeSQLEntry extends SQLEntry {

		<T extends SQLEntry> String getPreparedInsertSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getPreparedUpdateSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getPreparedDeleteSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getPreparedSelectSQL(DataBaseTable<T> table);

		// <T extends SQLEntry> String getPreparedQuerySQL(DataBaseTable<T> table);

		void prepareInsertSQL(PreparedStatement stmt) throws SQLException;

		void prepareUpdateSQL(PreparedStatement stmt) throws SQLException;

		void prepareDeleteSQL(PreparedStatement stmt) throws SQLException;

		void prepareSelectSQL(PreparedStatement stmt) throws SQLException;

		// void prepareQuerySQL(PreparedStatement stmt) throws SQLException;

	}

	public interface UnsafeSQLEntry extends SQLEntry {

		<T extends SQLEntry> String getInsertSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getUpdateSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getDeleteSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getSelectSQL(DataBaseTable<T> table);

		// <T extends SQLEntry> String getQuerySQL(DataBaseTable<T> table);

	}

}
