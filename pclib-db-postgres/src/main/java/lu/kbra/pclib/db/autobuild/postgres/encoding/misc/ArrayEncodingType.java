package lu.kbra.pclib.db.autobuild.postgres.encoding.misc;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public class ArrayEncodingType implements FixedEncodingType<Array> {

	@Override
	public Array getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getArray(columnIndex);
	}

	@Override
	public Array getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getArray(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Array value) throws SQLException {
		stmt.setArray(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.ARRAY;
	}

	@Override
	public String getTypeName() {
		return "ARRAY";
	}

}