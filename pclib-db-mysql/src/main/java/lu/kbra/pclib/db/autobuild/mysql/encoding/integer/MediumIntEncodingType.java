package lu.kbra.pclib.db.autobuild.mysql.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType.FixedUnsignedEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MediumIntEncodingType implements FixedUnsignedEncodingType<Integer> {

	private final boolean unsigned;

	@Override
	public Integer getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Integer value) throws SQLException {
		stmt.setInt(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.INTEGER;
	}

	@Override
	public String getRawTypeName() {
		return "MEDIUMINT";
	}

}
