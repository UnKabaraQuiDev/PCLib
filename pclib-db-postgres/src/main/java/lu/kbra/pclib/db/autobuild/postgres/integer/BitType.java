package lu.kbra.pclib.db.autobuild.postgres.integer;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class BitType implements FixedColumnType<Boolean> {

	@Override
	public Object decode(final Boolean value, final Type type) {
		if (type == Boolean.class || type == boolean.class) {
			return value.booleanValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Boolean encode(final Object value) {
		if (value instanceof Boolean) {
			return (boolean) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Boolean getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.BIT;
	}

	@Override
	public String getTypeName() {
		return "BIT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Boolean value) throws SQLException {
		stmt.setBoolean(index, value);
	}

}