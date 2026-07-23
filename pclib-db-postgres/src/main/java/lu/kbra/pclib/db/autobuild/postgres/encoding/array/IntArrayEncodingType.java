package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import lu.kbra.pclib.PCUtils;

public class IntArrayEncodingType implements ArrayEncodingType<int[]> {

	@Override
	public int[] getObject(ResultSet rs, int columnIndex) throws SQLException {
		return PCUtils.toPrimitiveInt((Integer[]) rs.getArray(columnIndex).getArray());
	}

	@Override
	public int[] getObject(ResultSet rs, String columnName) throws SQLException {
		return PCUtils.toPrimitiveInt((Integer[]) rs.getArray(columnName).getArray());
	}

	@Override
	public int getSQLType() {
		return Types.ARRAY;
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, int[] value) throws SQLException {
		stmt.setArray(index,
				stmt.getConnection()
						.createArrayOf(getRawTypeName(), Arrays.stream(value).mapToObj(Integer::valueOf).toArray(Integer[]::new)));
	}

	@Override
	public String getRawTypeName() {
		return "INTEGER";
	}

}
