package lu.kbra.pclib.db.autobuild.column.type.mysql;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public final class TextTypes {

	public static class CharType implements ColumnType {

		private final int length;

		public CharType(int length) {
			this.length = length;
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
		public Object variableValue() {
			return length;
		}

		@Override
		public int getSQLType() {
			return Types.CHAR;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Character) {
				return Character.toString((Character) value);
			} else if (value instanceof String) {
				return ((String) value).length() > length ? ((String) value).substring(0, length) : ((String) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Character.class) {
				return ((String) value).length() > 0 ? ((String) value).charAt(0) : null;
			} else if (type == String.class) {
				return (String) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, (String) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getString(columnName);
		}
	}

	public static class VarcharType implements ColumnType {

		private final int length;

		public VarcharType(int length) {
			this.length = length;
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
		public Object variableValue() {
			return length;
		}

		@Override
		public int getSQLType() {
			return Types.VARCHAR;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof String) {
				return (String) value;
			} else if (value instanceof CharSequence) {
				return (CharSequence) value;
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
		public Object decode(Object value, Type type) {
			if (value == null)
				return null;

			if (type == String.class) {
				return (String) value;
			} else if (type == CharSequence.class) {
				return (CharSequence) value;
			} else if (type == char[].class) {
				return ((String) value).toCharArray();
			} else if (type == byte[].class) {
				return ((String) value).getBytes();
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return Enum.valueOf((Class<Enum>) type, (String) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, (String) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getString(columnName);
		}

	}

	public static class UUIDType implements ColumnType {

		@Override
		public String getTypeName() {
			return "CHAR(36)";
		}

		@Override
		public boolean isVariable() {
			return false;
		}

		@Override
		public Object variableValue() {
			return null;
		}

		@Override
		public int getSQLType() {
			return Types.CHAR;
		}

		@Override
		public Object encode(Object value) {
			if (value == null) {
				return null;
			}
			if (value instanceof UUID) {
				return value.toString();
			}
			if (value instanceof String) {
				return value;
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (value == null) {
				return null;
			}
			if (type == UUID.class) {
				if (value instanceof String) {
					return UUID.fromString((String) value);
				}
			}
			if (type == String.class) {
				return value.toString();
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			if (value == null) {
				stmt.setNull(index, Types.CHAR);
			} else {
				stmt.setString(index, encode(value).toString());
			}
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			String str = rs.getString(columnIndex);
			return str != null ? UUID.fromString(str) : null;
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			String str = rs.getString(columnName);
			return str != null ? UUID.fromString(str) : null;
		}
	}

	public static class TextType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TEXT";
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof String) {
				return (String) value;
			} else if (value instanceof CharSequence) {
				return (CharSequence) value;
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
		public Object decode(Object value, Type type) {
			if (type == String.class) {
				return (String) value;
			} else if (type == CharSequence.class) {
				return (CharSequence) value;
			} else if (type == char[].class) {
				return ((String) value).toCharArray();
			} else if (type == byte[].class) {
				return ((String) value).getBytes();
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return Enum.valueOf((Class<Enum>) type, (String) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, (String) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getString(columnName);
		}

	}

	public static class JsonType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "JSON";
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof JSONObject) {
				return ((JSONObject) value).toString();
			} else if (value instanceof JSONArray) {
				return ((JSONArray) value).toString();
			} else if (value instanceof String) {
				// throws an exception if the string is not valid JSON
				return new JSONObject((String) value).toString();
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (value == null)
				return null;

			if (type == JSONObject.class) {
				return new JSONObject((String) value);
			} else if (type == JSONArray.class) {
				return new JSONArray((String) value);
			} else if (type == String.class) {
				return (String) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setString(index, (String) value);
		}

		@Override
		public String getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getString(columnName);
		}

	}

}
