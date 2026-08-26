package lu.kbra.pclib.db.autobuild.mysql.encoding.bool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class BooleanEncodingType implements FixedEncodingType<Boolean> {

	@Override
	public Boolean getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Boolean value) throws SQLException {
		stmt.setBoolean(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.BOOLEAN;
	}

	@Override
	public String getTypeName() {
		return "BOOLEAN";
	}

}
