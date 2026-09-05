package lu.kbra.pclib.db.autobuild.postgres.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

import lombok.Getter;

@Getter
public class TinyIntEncodingType implements FixedEncodingType<Byte> {

	@Override
	public Byte getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	@Override
	public Byte getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Byte value) throws SQLException {
		stmt.setByte(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.TINYINT;
	}

	@Override
	public String getTypeName() {
		return "TINYINT";
	}

}
