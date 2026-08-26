package lu.kbra.pclib.db.autobuild.mysql.encoding.text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.VariableEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VarcharEncodingType implements VariableEncodingType<String> {

	private final int length;

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
		stmt.setString(index, value);
	}

	@Override
	public int getSQLType() {
		return Types.VARCHAR;
	}

	@Override
	public String getTypeName() {
		return "VARCHAR";
	}

	@Override
	public Integer getVariableValue() {
		return this.length;
	}

}
