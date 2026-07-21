package lu.kbra.pclib.db.autobuild.mysql.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType.FixedUnsignedEncodingType;

@Getter
@RequiredArgsConstructor
public class TinyIntEncodingType implements FixedUnsignedEncodingType<Byte> {

	private final boolean unsigned;

	@Override
	public Byte getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	@Override
	public Byte getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Byte value) throws SQLException {
		stmt.setByte(index, value);
	}

	@Override
	public String getRawTypeName() {
		return "TINYINT";
	}

}
