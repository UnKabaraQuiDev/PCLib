package lu.pcy113.pclib.db.autobuild.column.type;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;
import lu.pcy113.pclib.impl.DependsOn;

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
				return (Character) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Character.class) {
				return (Character) value;
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

	@DependsOn("org.json.JSONObject")
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
			if(value == null)
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
