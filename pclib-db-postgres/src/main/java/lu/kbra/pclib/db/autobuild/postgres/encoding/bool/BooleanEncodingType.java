package lu.kbra.pclib.db.autobuild.postgres.encoding.bool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class BooleanEncodingType implements FixedEncodingType<Boolean> {

	@Override
	public Boolean getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Boolean value) throws SQLException {
		stmt.setBoolean(index, isVariable());
	}

	@Override
	public String getTypeName() {
		return "BOOLEAN";
	}

}
