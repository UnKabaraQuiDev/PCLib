package lu.kbra.pclib.db.autobuild.mysql.time.misc;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.YearMonth;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class YearMonthType implements FixedColumnType<Integer> {

	@Override
	public Object decode(final Integer value, final Type type) {
		if (value == null) {
			return null;
		}

		final int encoded = value;
		if (type == YearMonth.class) {
			return YearMonth.of(Math.floorDiv(encoded, 100), Math.floorMod(encoded, 100));
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Integer encode(final Object value) {
		if (value instanceof YearMonth) {
			final YearMonth yearMonth = (YearMonth) value;
			return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
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