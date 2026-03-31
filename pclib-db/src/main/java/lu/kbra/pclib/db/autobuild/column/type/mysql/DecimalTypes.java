package lu.kbra.pclib.db.autobuild.column.type.mysql;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public final class DecimalTypes {

	public static class DecimalType implements ColumnType {

		private final int precision;
		private final int scale;

		public DecimalType(final int precision, final int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		public DecimalType(final int precision, final String[] params) {
			this(precision, Integer.parseInt(params[0]));
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
			return this.precision + ", " + this.scale;
		}

		@Override
		public int getSQLType() {
			return Types.DECIMAL;
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof BigDecimal) {
				return value;
			} else if (value instanceof Number) {
				return value;
			} else if (value instanceof Float) {
				return (float) value;
			} else if (value instanceof Long) {
				return (long) value;
			} else if (value instanceof Integer) {
				return (int) value;
			} else if (value instanceof Short) {
				return (short) value;
			} else if (value instanceof Character) {
				return (char) value;
			} else if (value instanceof Byte) {
				return (byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (type == BigDecimal.class) {
				return value;
			} else if (type == Number.class) {
				return value;
			} else if (type == Double.class || type == double.class) {
				return ((BigDecimal) value).doubleValue();
			} else if (type == Float.class || type == float.class) {
				return ((BigDecimal) value).floatValue();
			} else if (type == Integer.class || type == int.class) {
				return ((BigDecimal) value).intValue();
			} else if (type == Short.class || type == short.class) {
				return ((BigDecimal) value).shortValue();
			} else if (type == Character.class || type == char.class) {
				return (char) ((BigDecimal) value).shortValue();
			} else if (type == Byte.class || type == byte.class) {
				return ((BigDecimal) value).byteValue();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			if (value instanceof BigDecimal) {
				stmt.setBigDecimal(index, (BigDecimal) value);
			} else if (value instanceof Double || value instanceof Float) {
				stmt.setBigDecimal(index, BigDecimal.valueOf((double) value));
			} else if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
				stmt.setBigDecimal(index, BigDecimal.valueOf((long) value));
			}
		}

		@Override
		public BigDecimal getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getBigDecimal(columnIndex);
		}

		@Override
		public BigDecimal getObject(final ResultSet rs, final String columnName) throws SQLException {
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
		public Object encode(final Object value) {
			if (value instanceof Double || value instanceof Float) {
				return (double) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (type == Double.class || type == double.class) {
				return (double) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setDouble(index, (double) value);
		}

		@Override
		public Double getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getDouble(columnIndex);
		}

		@Override
		public Double getObject(final ResultSet rs, final String columnName) throws SQLException {
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
		public Object encode(final Object value) {
			if (value instanceof Float) {
				return (float) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (type == Double.class || type == double.class) {
				return (float) value;
			} else if (type == Float.class || type == float.class) {
				return (float) value;
			} else if (type == Integer.class || type == int.class) {
				return (float) value;
			} else if (type == Short.class || type == short.class) {
				return (float) value;
			} else if (type == Long.class || type == long.class) {
				return (float) value;
			} else if (type == Byte.class || type == byte.class) {
				return (float) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setFloat(index, (float) value);
		}

		@Override
		public Float getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getFloat(columnIndex);
		}

		@Override
		public Float getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getFloat(columnName);
		}

	}

}
