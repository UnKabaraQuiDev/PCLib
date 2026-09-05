package lu.kbra.pclib.db.autobuild.mysql.encoding.decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FloatEncodingType implements FixedEncodingType<Float> {

	@Override
	public Float getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	@Override
	public Float getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Float value) throws SQLException {
		stmt.setFloat(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.FLOAT;
	}

	@Override
	public String getTypeName() {
		return "FLOAT";
	}

}
