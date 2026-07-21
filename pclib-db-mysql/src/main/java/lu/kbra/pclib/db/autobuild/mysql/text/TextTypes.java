package lu.kbra.pclib.db.autobuild.mysql;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public final class TextTypes {

	public static class CharType implements ColumnType<String> {

		private final int length;

		public CharType(final int length) {
			this.length = length;
		}

		public CharType(final Object object) {
			this.length = ColumnType.asInt(object);
		}

		@Override
		public Object decode(final String value, final Type type) {
			if (type == Character.class) {
				return value.length() > 0 ? value.charAt(0) : null;
			} else if (type == String.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public String encode(final Object value) {
			if (value instanceof Character) {
				return Character.toString((Character) value);
			} else if (value instanceof String) {
				return ((String) value).length() > this.length ? ((String) value).substring(0, this.length) : (String) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.CHAR;
		}

		@Override
		public String getTypeName() {
			return "CHAR";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
			stmt.setString(index, value);
		}

		@Override
		public Object variableValue() {
			return this.length;
		}
	}

	public static class JsonType implements FixedColumnType<String> {

		@Override
		public Object decode(final String value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == JSONObject.class) {
				return new JSONObject(value);
			} else if (type == JSONArray.class) {
				return new JSONArray(value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public String encode(final Object value) {
			if (value instanceof JSONObject) {
				return ((JSONObject) value).toString();
			} else if (value instanceof JSONArray) {
				return ((JSONArray) value).toString();
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}

		@Override
		public String getTypeName() {
			return "JSON";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
			stmt.setString(index, value);
		}

	}

	public static class TextType implements FixedColumnType<String> {

		@Override
		public Object decode(final String value, final Type type) {
			if (type == String.class) {
				return value;
			} else if (type == CharSequence.class) {
				return value;
			} else if (type == char[].class) {
				return value.toCharArray();
			} else if (type == byte[].class) {
				return value.getBytes();
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return Enum.valueOf((Class<Enum>) type, value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public String encode(final Object value) {
			if (value instanceof String) {
				return (String) value;
			} else if (value instanceof CharSequence) {
				return ((CharSequence) value).toString();
			} else if (value instanceof char[]) {
				return new String((char[]) value);
			} else if (value instanceof byte[]) {
				return new String((byte[]) value);
			} else if (value instanceof Enum<?>) {
				return ((Enum<?>) value).name();
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}

		@Override
		public String getTypeName() {
			return "TEXT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
			stmt.setString(index, value);
		}

	}

	public static class UUIDType implements FixedColumnType<String> {

		@Override
		public Object decode(final String value, final Type type) {
			if (value == null) {
				return null;
			} else if (type == UUID.class) {
				return UUID.fromString(value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public String encode(final Object value) {
			if (value == null) {
				return null;
			} else if (value instanceof UUID) {
				return value.toString();
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.CHAR;
		}

		@Override
		public String getTypeName() {
			return "CHAR(36)";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
			stmt.setString(index, value);
		}

	}

	public static class VarcharType implements ColumnType<String> {

		private final int length;

		public VarcharType(final int length) {
			this.length = length;
		}

		public VarcharType(final Object object) {
			this.length = ColumnType.asInt(object);
		}

		@Override
		public Object decode(final String value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == String.class) {
				return value;
			} else if (type == CharSequence.class) {
				return value;
			} else if (type == char[].class) {
				return value.toCharArray();
			} else if (type == byte[].class) {
				return value.getBytes();
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return Enum.valueOf((Class<Enum>) type, value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public String encode(final Object value) {
			if (value instanceof String) {
				return (String) value;
			} else if (value instanceof CharSequence) {
				return ((CharSequence) value).toString();
			} else if (value instanceof char[]) {
				return new String((char[]) value);
			} else if (value instanceof byte[]) {
				return new String((byte[]) value);
			} else if (value instanceof Enum<?>) {
				return ((Enum<?>) value).name();
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.VARCHAR;
		}

		@Override
		public String getTypeName() {
			return "VARCHAR";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
			stmt.setString(index, value);
		}

		@Override
		public Object variableValue() {
			return this.length;
		}

	}

}
