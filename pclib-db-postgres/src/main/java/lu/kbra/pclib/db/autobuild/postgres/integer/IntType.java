package lu.kbra.pclib.db.autobuild.postgres.integer;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class IntType implements FixedColumnType<Integer> {

	@Override
	public Object decode(final Integer value, final Type type) {
		if (type == Integer.class || type == int.class) {
			return (int) value;
		} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
			return PCUtils.valueOfOrdinal(((Class<?>) type).asSubclass(Enum.class), value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Integer encode(final Object value) {
		if (value instanceof Integer) {
			return (int) value;
		} else if (value instanceof Enum<?>) {
			return ((Enum<?>) value).ordinal();
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Integer getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.INTEGER;
	}

	@Override
	public String getTypeName() {
		return "INT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
		stmt.setInt(index, value);
	}

}