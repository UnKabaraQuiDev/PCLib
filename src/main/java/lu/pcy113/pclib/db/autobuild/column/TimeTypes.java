package lu.pcy113.pclib.db.autobuild.column;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class TimeTypes {

	public static class DateType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "DATE";
		}

		@Override
		public int getSQLType() {
			return Types.DATE;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Date) {
				return (Date) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setDate(index, (Date) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getDate(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getDate(columnName);
		}

	}

	public static class TimestampType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TIMESTAMP";
		}

		@Override
		public int getSQLType() {
			return Types.TIMESTAMP;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Timestamp) {
				return (Timestamp) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setTimestamp(index, (Timestamp) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getTimestamp(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getTimestamp(columnName);
		}

	}

}
