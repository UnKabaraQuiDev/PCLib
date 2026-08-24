package lu.kbra.pclib.db.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QueryParameter<T> {

	private final ColumnType<T, ?> type;
	private final T value;

	public void store(final PreparedStatement stmt, final int index) throws SQLException {
		this.type.store(stmt, index, this.value);
	}

}
