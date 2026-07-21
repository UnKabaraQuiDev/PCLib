package lu.kbra.pclib.db.autobuild.mysql.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class TimeEncoding implements FixedEncodingType<Time> {

	@Override
	public Time getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	@Override
	public Time getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Time value) throws SQLException {
		stmt.setTime(index, value);
	}

	@Override
	public String getTypeName() {
		return "TIME";
	}

}
