package lu.kbra.pclib.db.autobuild.mysql;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public final class DecimalTypes {

	public static class DecimalType implements ColumnType<BigDecimal> {

		private final int precision;
		private final int scale;

		public DecimalType(final int precision, final int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		@Deprecated
		public DecimalType(final int precision, final String[] params) {
			this(precision, Integer.parseInt(params[0]));
		}

		public DecimalType(final Object precision, final Object scale) {
			this(ColumnType.asInt(precision), ColumnType.asInt(scale));
		}

		@Override
		public Object decode(final BigDecimal value, final Type type) {
			if (type == BigDecimal.class) {
				return value;
			} else if (type == Double.class || type == double.class) {
				return value.doubleValue();
			} else if (type == Float.class || type == float.class) {
				return value.floatValue();
			} else if (type == Integer.class || type == int.class) {
				return value.intValue();
			} else if (type == Short.class || type == short.class) {
				return value.shortValue();
			} else if (type == Character.class || type == char.class) {
				return (char) value.shortValue();
			} else if (type == Byte.class || type == byte.class) {
				return value.byteValue();
			} else if (type == Number.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public BigDecimal encode(final Object value) {
			if (value instanceof BigDecimal) {
				return (BigDecimal) value;
			} else if (value instanceof Float) {
				return BigDecimal.valueOf((float) value);
			} else if (value instanceof Long) {
				return BigDecimal.valueOf((long) value);
			} else if (value instanceof Integer) {
				return BigDecimal.valueOf((int) value);
			} else if (value instanceof Short) {
				return BigDecimal.valueOf((short) value);
			} else if (value instanceof Character) {
				return BigDecimal.valueOf((char) value);
			} else if (value instanceof Byte) {
				return BigDecimal.valueOf((byte) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public BigDecimal getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getBigDecimal(columnIndex);
		}

		@Override
		public BigDecimal getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getBigDecimal(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.DECIMAL;
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
		public void setObject(final PreparedStatement stmt, final int index, final BigDecimal value) throws SQLException {
			stmt.setBigDecimal(index, value);
		}

		@Override
		public String variableValue() {
			return this.precision + ", " + this.scale;
		}

	}

	public static class DoubleType implements FixedColumnType<Double> {

		@Override
		public Object decode(final Double value, final Type type) {
			if (type == Double.class || type == double.class) {
				return (double) value.doubleValue();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Double encode(final Object value) {
			if (value instanceof Double || value instanceof Float) {
				return (double) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Double getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getDouble(columnIndex);
		}

		@Override
		public Double getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getDouble(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.DOUBLE;
		}

		@Override
		public String getTypeName() {
			return "DOUBLE";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Double value) throws SQLException {
			stmt.setDouble(index, value);
		}

	}

	public static class FloatType implements FixedColumnType<Float> {

		@Override
		public Object decode(final Float value, final Type type) {
			if (type == Float.class || type == float.class) {
				return value.floatValue();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Float encode(final Object value) {
			if (value instanceof Float) {
				return (Float) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Float getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getFloat(columnIndex);
		}

		@Override
		public Float getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getFloat(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.FLOAT;
		}

		@Override
		public String getTypeName() {
			return "FLOAT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Float value) throws SQLException {
			stmt.setFloat(index, value);
		}

	}

}
