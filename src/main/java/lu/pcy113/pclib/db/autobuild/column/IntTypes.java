package lu.pcy113.pclib.db.autobuild.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BitType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIT";
		}

		@Override
		public int getSQLType() {
			return Types.BIT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Boolean) {
				return (boolean) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBoolean(index, (boolean) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getBoolean(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getBoolean(columnName);
		}

	}

	public static class SmallIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

		@Override
		public int getSQLType() {
			return Types.SMALLINT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Short) {
				return (short) value;
			} else if (value instanceof Byte) {
				return (short) (Byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setShort(index, (short) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getShort(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getShort(columnName);
		}

	}

	public static class IntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "INT";
		}

		@Override
		public int getSQLType() {
			return Types.INTEGER;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Integer) {
				return (int) value;
			} else if (value instanceof Short) {
				return (int) (Short) value;
			} else if (value instanceof Byte) {
				return (int) (Byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setInt(index, (int) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getInt(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getInt(columnName);
		}

	}

	public static class BigIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIGINT";
		}

		@Override
		public int getSQLType() {
			return Types.BIGINT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Long) {
				return (long) value;
			} else if (value instanceof Integer) {
				return (long) (Integer) value;
			} else if (value instanceof Short) {
				return (long) (Short) value;
			} else if (value instanceof Byte) {
				return (long) (Byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setLong(index, (long) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getLong(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getLong(columnName);
		}

	}

}
