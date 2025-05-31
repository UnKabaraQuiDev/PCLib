package lu.pcy113.pclib.db.autobuild.column.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType;
import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public class BooleanType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "BOOLEAN";
	}

	@Override
	public int getSQLType() {
		return Types.BOOLEAN;
	}

	@Override
	public Object encode(Object value) {
		if (value instanceof Boolean) {
			return (boolean) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(Object value, Class<?> type) {
		if (type == Boolean.class || type == boolean.class) {
			return (boolean) value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
		stmt.setBoolean(index, (boolean) value);
	}

	@Override
	public Boolean getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

}
