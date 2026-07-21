package lu.kbra.pclib.db.autobuild.postgres.text;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class JsonType implements FixedColumnType<String> {

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
