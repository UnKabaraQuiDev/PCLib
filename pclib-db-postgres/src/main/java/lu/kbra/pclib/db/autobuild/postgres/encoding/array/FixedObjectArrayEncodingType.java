package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FixedObjectArrayEncodingType implements FixedArrayEncodingType<Object> {

	private final String rawTypeName;
	private final int[] dimensions;

	@Override
	public Object getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getArray(columnIndex);
	}

	@Override
	public Object getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getArray(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.ARRAY;
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setArray(index, stmt.getConnection().createArrayOf(this.getRawTypeName(), Object[].class.cast(value)));
	}

}
