package lu.kbra.pclib.db.autobuild.postgres.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class TimestampEncodingType implements FixedEncodingType<Timestamp> {

	@Override
	public Timestamp getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	@Override
	public Timestamp getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Timestamp value) throws SQLException {
		stmt.setTimestamp(index, value);
	}

	@Override
	public String getTypeName() {
		return "TIMESTAMP";
	}

}
