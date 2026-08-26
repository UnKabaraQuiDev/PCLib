package lu.kbra.pclib.db.autobuild.mysql.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class YearEncodingType implements FixedEncodingType<Integer> {

	@Override
	public Integer getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
		stmt.setInt(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.SMALLINT;
	}

	@Override
	public String getTypeName() {
		return "YEAR";
	}

}
