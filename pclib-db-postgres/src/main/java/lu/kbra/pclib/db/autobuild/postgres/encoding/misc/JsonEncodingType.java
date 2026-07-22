package lu.kbra.pclib.db.autobuild.postgres.encoding.misc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class JsonEncodingType implements FixedEncodingType<String> {

	@Override
	public String getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, String value) throws SQLException {
		stmt.setString(index, value);
	}

	@Override
	public String getTypeName() {
		return "JSON";
	}

}
