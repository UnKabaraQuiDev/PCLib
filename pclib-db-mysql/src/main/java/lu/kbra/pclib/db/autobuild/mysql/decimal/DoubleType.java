package lu.kbra.pclib.db.autobuild.mysql.decimal;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class DoubleType implements FixedColumnType<Double> {

	@Override
	public Object decode(final Double value, final Type type) {
		if (type == Double.class || type == double.class) {
			return (double) value.doubleValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Double encode(final Object value) {
		if (value instanceof Double || value instanceof Float) {
			return (double) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Double getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public Double getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.DOUBLE;
	}

	@Override
	public String getTypeName() {
		return "DOUBLE";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Double value) throws SQLException {
		stmt.setDouble(index, value);
	}

}