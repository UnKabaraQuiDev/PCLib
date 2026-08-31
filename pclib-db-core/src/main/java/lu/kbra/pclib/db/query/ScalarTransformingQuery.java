package lu.kbra.pclib.db.query;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ScalarTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

	private final String sql;
	private final ColumnType<Object, ?>[] paramTypes;
	private final Object[] paramValues;
	private final Query.Type type;
	private final int[] reordering;
	private final ColumnType<B, ?> returnColumnType;
	private final Type returnType;

	@Override
	public String getPreparedQuerySQL(final SQLQueryable<T> table) {
		return this.sql;
	}

	@Override
	public B transform(final SQLQueryable<T> table, final ResultSet rs) throws SQLException {
		final List<Object> data = new ArrayList<>();
		while (rs.next()) {
			TransformingQuery.transformRow(data, this.type, () -> this.returnColumnType.load(rs, 1, this.returnType));
		}
		return TransformingQuery.transform(data, this.type);
	}

	@Override
	public void updateQuerySQL(final SQLQueryable<T> table, final PreparedStatement stmt) throws SQLException {
		int i = 0;
		for (final int t : this.reordering) {
			this.paramTypes[t].store(stmt, i + 1, this.paramValues[t]);
			i++;
		}
	}

}
