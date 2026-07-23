package lu.kbra.pclib.db.autobuild.mysql.encoding.decimal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.VariableEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DecimalEncodingType implements VariableEncodingType<BigDecimal> {

	private final int precision;
	private final int scale;

	@Override
	public BigDecimal getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, BigDecimal value) throws SQLException {
		stmt.setBigDecimal(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.DECIMAL;
	}

	@Override
	public String getTypeName() {
		return "DECIMAL";
	}

	@Override
	public Object variableValue() {
		return precision + ", " + scale;
	}

}
