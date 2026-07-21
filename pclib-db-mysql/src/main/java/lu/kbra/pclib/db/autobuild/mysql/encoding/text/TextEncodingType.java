package lu.kbra.pclib.db.autobuild.mysql.encoding.text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;
import lu.kbra.pclib.db.domain.column.type.SizeClass;

@Getter
@RequiredArgsConstructor
public class TextEncodingType implements FixedEncodingType<String> {

	private final SizeClass sizeClass;

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
	public String getTypeName() {
		return sizeClass.asSql() + "TEXT";
	}

}
