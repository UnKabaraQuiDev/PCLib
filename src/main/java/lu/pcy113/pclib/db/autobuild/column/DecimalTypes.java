package lu.pcy113.pclib.db.autobuild.column;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class DecimalTypes {

	public static class DecimalType implements ColumnType {

		private final int precision;
		private final int scale;

		public DecimalType(int precision, int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		@Override
		public String getTypeName() {
			return "DECIMAL";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public Object variableValue() {
			return precision + ", " + scale;
		}

		@Override
		public int getSQLType() {
			return Types.DECIMAL;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof BigDecimal) {
				return (BigDecimal) value;
			} else if (value instanceof Number) {
				return (Number) value;
			}
			/*
			 * else if (value instanceof Float) { return (float) value; } else if (value
			 * instanceof Long) { return (long) value; } else if (value instanceof Integer)
			 * { return (int) value; } else if (value instanceof Short) { return (short)
			 * value; } else if (value instanceof Byte) { return (byte) value; }
			 */

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			if (value instanceof BigDecimal) {
				stmt.setBigDecimal(index, (BigDecimal) value);
			} else if (value instanceof Double || value instanceof Float) {
				stmt.setBigDecimal(index, BigDecimal.valueOf((double) value));
			} else if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
				stmt.setBigDecimal(index, BigDecimal.valueOf((long) value));
			}
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getBigDecimal(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getBigDecimal(columnName);
		}

	}

	public static class DoubleType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "DOUBLE";
		}

		@Override
		public int getSQLType() {
			return Types.DOUBLE;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Double || value instanceof Float) {
				return (double) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setDouble(index, (double) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getDouble(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getDouble(columnName);
		}

	}

	public static class FloatType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "FLOAT";
		}

		@Override
		public int getSQLType() {
			return Types.FLOAT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Float) {
				return (float) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setFloat(index, (float) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getFloat(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getFloat(columnName);
		}

	}

}
