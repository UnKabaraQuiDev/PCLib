package lu.kbra.pclib.db.autobuild.postgres.decimal;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class FloatType implements FixedColumnType<Float> {

	@Override
	public Object decode(final Float value, final Type type) {
		if (type == Float.class || type == float.class) {
			return value.floatValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Float encode(final Object value) {
		if (value instanceof Float) {
			return (Float) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Float getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	@Override
	public Float getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.FLOAT;
	}

	@Override
	public String getTypeName() {
		return "FLOAT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Float value) throws SQLException {
		stmt.setFloat(index, value);
	}

}