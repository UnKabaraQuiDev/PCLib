package lu.kbra.pclib.db.autobuild.mysql.encoding.misc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.domain.column.type.EncodingType.VariableEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SetEncodingType implements VariableEncodingType<String> {

	private final String[] names;

	@Override
	public String getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, String value) throws SQLException {
		stmt.setString(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.LONGVARCHAR;
	}

	@Override
	public String getTypeName() {
		return "SET";
	}

	@Override
	public Object getVariableValue() {
		return Arrays.stream(names).map(c -> String.format("'%s'", c)).collect(Collectors.joining(","));
	}

}
