package lu.kbra.pclib.db.autobuild.column.type.mysql;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

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
			} else if (value instanceof LocalDate) {
				return Date.valueOf((LocalDate) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Date.class) {
				return (Date) value;
			} else if (type == Timestamp.class) {
				return PCUtils.toTimestamp((Date) value);
			} else if (type == LocalDate.class) {
				return ((Date) value).toLocalDate();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setDate(index, (Date) value);
		}

		@Override
		public Date getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getDate(columnIndex);
		}

		@Override
		public Date getObject(ResultSet rs, String columnName) throws SQLException {
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
			} else if (value instanceof LocalDateTime) {
				return Timestamp.valueOf((LocalDateTime) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Timestamp.class) {
				return (Timestamp) value;
			} else if (type == LocalDateTime.class) {
				return ((Timestamp) value).toLocalDateTime();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setTimestamp(index, (Timestamp) value);
		}

		@Override
		public Timestamp getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getTimestamp(columnIndex);
		}

		@Override
		public Timestamp getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getTimestamp(columnName);
		}

	}

}
