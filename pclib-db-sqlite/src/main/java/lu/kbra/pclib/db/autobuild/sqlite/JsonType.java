package lu.kbra.pclib.db.autobuild.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public class JsonType implements FixedColumnType {

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}
		if (type == JSONObject.class) {
			return new JSONObject((String) value);
		} else if (type == JSONArray.class) {
			return new JSONArray((String) value);
		} else if (type == String.class) {
			return value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof JSONObject) {
			return ((JSONObject) value).toString();
		} else if (value instanceof JSONArray) {
			return ((JSONArray) value).toString();
		} else if (value instanceof String) {
			final String text = (String) value;
			try {
				return new JSONObject(text).toString();
			} catch (final RuntimeException objectException) {
				return new JSONArray(text).toString();
			}
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
		return "TEXT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}

}
