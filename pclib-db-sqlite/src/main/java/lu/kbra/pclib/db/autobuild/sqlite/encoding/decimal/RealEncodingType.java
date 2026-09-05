package lu.kbra.pclib.db.autobuild.sqlite.encoding.decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RealEncodingType implements FixedEncodingType<Double> {

	@Override
	public Double getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public Double getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Double value) throws SQLException {
		stmt.setDouble(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.REAL;
	}

	@Override
	public String getTypeName() {
		return "REAL";
	}

}
