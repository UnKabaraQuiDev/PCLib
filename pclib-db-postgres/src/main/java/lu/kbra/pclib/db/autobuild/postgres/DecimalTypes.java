package lu.kbra.pclib.db.autobuild.postgres;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public final class DecimalTypes {

	public static class DoublePrecisionType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			if (type == Double.class || type == double.class) {
				return ((Number) value).doubleValue();
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
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
			return "DOUBLE PRECISION";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setDouble(index, ((Number) value).doubleValue());
		}
	}

	public static class NumericType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final BigDecimal decimal = (BigDecimal) value;
			if (type == BigDecimal.class || type == Number.class) {
				return decimal;
			} else if (type == Double.class || type == double.class) {
				return decimal.doubleValue();
			} else if (type == Float.class || type == float.class) {
				return decimal.floatValue();
			} else if (type == Long.class || type == long.class) {
				return decimal.longValue();
			} else if (type == Integer.class || type == int.class) {
				return decimal.intValue();
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof BigDecimal) {
				return value;
			} else if (value instanceof Number) {
				return BigDecimal.valueOf(((Number) value).doubleValue());
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
			return Types.NUMERIC;
		}

		@Override
		public String getTypeName() {
			return "NUMERIC";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			if (value instanceof BigDecimal) {
				stmt.setBigDecimal(index, (BigDecimal) value);
			} else {
				stmt.setBigDecimal(index, BigDecimal.valueOf(((Number) value).doubleValue()));
			}
		}
	}

	public static class RealType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final float floatValue = ((Number) value).floatValue();
			if (type == Double.class || type == double.class) {
				return (double) floatValue;
			} else if (type == Float.class || type == float.class) {
				return floatValue;
			} else if (type == Integer.class || type == int.class) {
				return (int) floatValue;
			} else if (type == Short.class || type == short.class) {
				return (short) floatValue;
			} else if (type == Long.class || type == long.class) {
				return (long) floatValue;
			} else if (type == Byte.class || type == byte.class) {
				return (byte) floatValue;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
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
			return "REAL";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setFloat(index, ((Number) value).floatValue());
		}
	}

	private DecimalTypes() {
	}
}
