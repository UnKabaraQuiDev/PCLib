package lu.kbra.pclib.db.autobuild.postgres.encoding.temporal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class DateEncodingType implements FixedEncodingType<Date> {

	@Override
	public Date getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public Date getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Date value) throws SQLException {
		stmt.setDate(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.DATE;
	}

	@Override
	public String getTypeName() {
		return "DATE";
	}

}
