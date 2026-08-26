package lu.kbra.pclib.db.autobuild.mysql.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class TimeEncodingType implements FixedEncodingType<Time> {

	@Override
	public Time getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	@Override
	public Time getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Time value) throws SQLException {
		stmt.setTime(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.TIME;
	}

	@Override
	public String getTypeName() {
		return "TIME";
	}

}
