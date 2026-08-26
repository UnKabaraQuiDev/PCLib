package lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class DatetimeEncodingType implements FixedEncodingType<String> {

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
		stmt.setString(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.TIMESTAMP;
	}

	@Override
	public String getTypeName() {
		return "DATETIME";
	}

}
