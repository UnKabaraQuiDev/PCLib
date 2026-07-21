package lu.kbra.pclib.db.autobuild.mysql.encoding.temporal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class DateEncoding implements FixedEncodingType<Date> {

	@Override
	public Date getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public Date getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Date value) throws SQLException {
		stmt.setDate(index, value);
	}

	@Override
	public String getTypeName() {
		return "DATE";
	}

}
