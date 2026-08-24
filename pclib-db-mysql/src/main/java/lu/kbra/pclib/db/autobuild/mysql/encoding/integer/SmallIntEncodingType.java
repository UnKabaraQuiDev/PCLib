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
public class SmallIntEncodingType implements FixedUnsignedEncodingType<Short> {

	private final boolean unsigned;

	@Override
	public Short getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	@Override
	public Short getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Short value) throws SQLException {
		stmt.setShort(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.SMALLINT;
	}

	@Override
	public String getRawTypeName() {
		return "SMALLINT";
	}

}
