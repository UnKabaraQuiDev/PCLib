package lu.kbra.pclib.db.migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MigrationEncodingType implements EncodingType<Void> {

	private final String typeName;

	@Override
	public Void getObject(ResultSet rs, int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Void getObject(ResultSet rs, String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getSQLType() {
		return 0;
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Void value) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isVariable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getVariableValue() {
		throw new UnsupportedOperationException();
	}

	public Class<Void> getJdbcType() {
		return Void.class;
	}

}
