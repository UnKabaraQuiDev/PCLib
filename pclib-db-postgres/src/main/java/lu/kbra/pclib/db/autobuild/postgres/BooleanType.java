package lu.kbra.pclib.db.autobuild.postgres;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public class BooleanType implements FixedColumnType {

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}
		if (type == Boolean.class || type == boolean.class) {
			return (boolean) value;
		}
		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
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
		return Types.BOOLEAN;
	}

	@Override
	public String getTypeName() {
		return "BOOLEAN";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setBoolean(index, (boolean) value);
	}
}
