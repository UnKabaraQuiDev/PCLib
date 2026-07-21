package lu.kbra.pclib.db.autobuild.mysql.encoding.temporal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class YearEncodingType implements FixedEncodingType<Integer> {

	@Override
	public Integer getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Integer value) throws SQLException {
		stmt.setInt(index, value);
	}

	@Override
	public String getTypeName() {
		return "YEAR";
	}

}
