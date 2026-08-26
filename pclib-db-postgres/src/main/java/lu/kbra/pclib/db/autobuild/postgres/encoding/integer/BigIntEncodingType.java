package lu.kbra.pclib.db.autobuild.postgres.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

import lombok.Getter;

@Getter
public class BigIntEncodingType implements FixedEncodingType<Long> {

	@Override
	public Long getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	@Override
	public Long getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Long value) throws SQLException {
		stmt.setLong(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.BIGINT;
	}

	@Override
	public String getTypeName() {
		return "BIGINT";
	}

}
