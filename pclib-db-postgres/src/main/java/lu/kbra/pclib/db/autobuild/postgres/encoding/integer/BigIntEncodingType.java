package lu.kbra.pclib.db.autobuild.postgres.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lombok.Getter;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

@Getter
public class BigIntEncodingType implements FixedEncodingType<Long> {

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
	public int getSQLType() {
		return Types.BIGINT;
	}

	@Override
	public String getTypeName() {
		return "BIGINT";
	}

}
