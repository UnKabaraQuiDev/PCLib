package lu.kbra.pclib.db.autobuild.mysql.encoding.misc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class XmlEncodingType implements FixedEncodingType<String> {

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
	public int getSQLType() {
		return Types.SQLXML;
	}

	@Override
	public String getTypeName() {
		return "XML";
	}

}
