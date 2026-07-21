package lu.kbra.pclib.db.autobuild.postgres.time.misc;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Year;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class YearType implements FixedColumnType<Integer> {

	@Override
	public Object decode(final Integer value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == Year.class) {
			return Year.of(value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Integer encode(final Object value) {
		if (value instanceof Year) {
			return ((Year) value).getValue();
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
		return "INTEGER";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
		stmt.setInt(index, value);
	}

}