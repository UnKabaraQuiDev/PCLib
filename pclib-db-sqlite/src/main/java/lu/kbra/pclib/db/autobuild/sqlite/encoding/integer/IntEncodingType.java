package lu.kbra.pclib.db.autobuild.sqlite.encoding.integer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

@Getter
@RequiredArgsConstructor
public class IntEncodingType implements FixedEncodingType<Long> {

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
	public String getTypeName() {
		return "INTEGER";
	}

}
