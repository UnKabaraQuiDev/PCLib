package lu.kbra.pclib.db.autobuild.postgres.encoding.decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RealEncodingType implements FixedEncodingType<Float> {

	@Override
	public Float getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	@Override
	public Float getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Float value) throws SQLException {
		stmt.setFloat(index, value);
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
