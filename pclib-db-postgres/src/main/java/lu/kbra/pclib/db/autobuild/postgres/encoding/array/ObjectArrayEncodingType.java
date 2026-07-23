package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lombok.Getter;

@Getter
public class ObjectArrayEncodingType<T> implements ArrayEncodingType<T> {

	private final String rawTypeName;
	private final Class<?> arrayType;
	private final int dimensionCount;

	public ObjectArrayEncodingType(String rawTypeName, Class<?> arrayType, int dimensionCount) {
		if (!arrayType.isArray()) {
			throw new IllegalArgumentException("Type: " + arrayType + " is not an array.");
		}
		this.rawTypeName = rawTypeName;
		this.arrayType = arrayType;
		this.dimensionCount = dimensionCount;
	}

	@Override
	public T getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return (T) rs.getArray(columnIndex).getArray();
	}

	@Override
	public T getObject(final ResultSet rs, final String columnName) throws SQLException {
		return (T) rs.getArray(columnName).getArray();
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
