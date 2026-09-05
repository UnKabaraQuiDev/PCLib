package lu.kbra.pclib.db.autobuild.postgres.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetTime;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class TimeZEncodingType implements FixedEncodingType<OffsetTime> {

	@Override
	public OffsetTime getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getObject(columnIndex, OffsetTime.class);
	}

	@Override
	public OffsetTime getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getObject(columnName, OffsetTime.class);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final OffsetTime value) throws SQLException {
		stmt.setObject(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.TIME_WITH_TIMEZONE;
	}

	@Override
	public String getTypeName() {
		return "TIME WITH TIME ZONE";
	}

}
