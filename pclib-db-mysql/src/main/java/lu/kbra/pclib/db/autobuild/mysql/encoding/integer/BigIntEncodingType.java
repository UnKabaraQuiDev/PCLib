package lu.kbra.pclib.db.autobuild.mysql.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType.FixedUnsignedEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BigIntEncodingType implements FixedUnsignedEncodingType<Long> {

	private final boolean unsigned;

	@Override
	public Long getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	@Override
	public Long getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Long value) throws SQLException {
		stmt.setLong(index, value);
	}

	@Override
	public String getRawTypeName() {
		return "BIGINT";
	}

}
