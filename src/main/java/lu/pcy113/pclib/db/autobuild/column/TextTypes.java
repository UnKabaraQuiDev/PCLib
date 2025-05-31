package lu.pcy113.pclib.db.autobuild.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;
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
			}

			return ColumnType.unsupported(value);
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
			}

			return ColumnType.unsupported(value);
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

}
