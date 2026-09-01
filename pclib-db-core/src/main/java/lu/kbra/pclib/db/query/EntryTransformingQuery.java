package lu.kbra.pclib.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.loader.ResultSetIterator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EntryTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

	private final String sql;
	private final ColumnType<Object, ?>[] paramTypes;
	private final Object[] paramValues;
	private final Query.Type type;
	private final int[] reordering;
	private final SQLQueryable<T> returnTypeOwner;
//	private final Class<T> returnType;

	@Override
	public String getPreparedQuerySQL(final SQLQueryable<T> table) {
		return this.sql;
	}

	@Override
	public B transform(final SQLQueryable<T> table, final ResultSet rs) throws SQLException {
		final List<Object> data = new ArrayList<>();
		final Iterator<T> it = new ResultSetIterator<>(returnTypeOwner, rs);
		while (it.hasNext()) {
			TransformingQuery.transformRow(data, this.type, it::next);
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
