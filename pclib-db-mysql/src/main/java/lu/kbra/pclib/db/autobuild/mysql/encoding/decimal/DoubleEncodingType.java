package lu.kbra.pclib.db.autobuild.mysql.encoding.decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lombok.Getter;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

@Getter
public class DoubleEncodingType implements FixedEncodingType<Double> {

	@Override
	public Double getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public Double getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Double value) throws SQLException {
		stmt.setDouble(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.DOUBLE;
	}

	@Override
	public String getTypeName() {
		return "DOUBLE";
	}

}
