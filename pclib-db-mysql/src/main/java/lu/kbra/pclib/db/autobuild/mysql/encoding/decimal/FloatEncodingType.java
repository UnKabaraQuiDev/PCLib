package lu.kbra.pclib.db.autobuild.mysql.encoding.decimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

@Getter
@RequiredArgsConstructor
public class FloatEncodingType implements FixedEncodingType<Float> {

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
	public String getTypeName() {
		return "FLOAT";
	}


}
