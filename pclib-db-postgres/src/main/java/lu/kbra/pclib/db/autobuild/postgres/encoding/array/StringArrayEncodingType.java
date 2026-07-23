package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class StringArrayEncodingType implements ArrayEncodingType<String[]> {

	@Override
	public String[] getObject(ResultSet rs, int columnIndex) throws SQLException {
		return (String[]) rs.getArray(columnIndex).getArray();
	}

	@Override
	public String[] getObject(ResultSet rs, String columnName) throws SQLException {
		return (String[]) rs.getArray(columnName).getArray();
	}

	@Override
	public int getSQLType() {
		return Types.ARRAY;
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, String[] value) throws SQLException {
		stmt.setArray(index, stmt.getConnection().createArrayOf(getRawTypeName(), value));
	}

	@Override
	public String getRawTypeName() {
		return "TEXT";
	}

}
