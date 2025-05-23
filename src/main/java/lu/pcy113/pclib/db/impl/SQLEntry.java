package lu.pcy113.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import lu.pcy113.pclib.db.DataBaseTable;

public interface SQLEntry extends Cloneable {

	public interface SafeSQLEntry extends SQLEntry {

		<T extends SQLEntry> String getPreparedInsertSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getPreparedUpdateSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getPreparedDeleteSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getPreparedSelectSQL(SQLQueryable<T> table);

		void prepareInsertSQL(PreparedStatement stmt) throws SQLException;

		void prepareUpdateSQL(PreparedStatement stmt) throws SQLException;

		void prepareDeleteSQL(PreparedStatement stmt) throws SQLException;

		void prepareSelectSQL(PreparedStatement stmt) throws SQLException;

	}

	public interface UnsafeSQLEntry extends SQLEntry {

		<T extends SQLEntry> String getInsertSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getUpdateSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getDeleteSQL(DataBaseTable<T> table);

		<T extends SQLEntry> String getSelectSQL(SQLQueryable<T> table);

	}

	public interface ReadOnlySQLEntry extends SQLEntry {

		public interface SafeReadOnlySQLEntry extends ReadOnlySQLEntry {

			<T extends SQLEntry> String getPreparedSelectSQL(SQLQueryable<T> table);

			void prepareSelectSQL(PreparedStatement stmt) throws SQLException;

		}

		public interface UnsafeReadOnlySQLEntry extends ReadOnlySQLEntry {

			<T extends SQLEntry> String getSelectSQL(SQLQueryable<T> table);

		}

	}

}
