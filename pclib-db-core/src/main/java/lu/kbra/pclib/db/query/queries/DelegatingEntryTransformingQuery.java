package lu.kbra.pclib.db.query.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.loader.EntryResultSetEnumeration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DelegatingEntryTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

	private final String sql;
	private final ColumnType<Object, ?>[] paramTypes;
	private final Object[] paramValues;
	private final Query.Type type;
	private final int[] reordering;
	private final SQLQueryable<T> returnTypeOwner;

	private final BiFunction<Query.Type, Enumeration<T>, B> delegateFunction;

	@Override
	public String getPreparedQuerySQL(final SQLQueryable<T> table) {
		return this.sql;
	}

	@Override
	public B transform(final SQLQueryable<T> table, final ResultSet rs) throws SQLException {
		final EntryResultSetEnumeration<T> it = new EntryResultSetEnumeration<>(this.returnTypeOwner, rs);
		it.setCloseStatement(true);
		return this.delegateFunction.apply(this.type, it);
	}

	@Override
	public void updateQuerySQL(final SQLQueryable<T> table, final PreparedStatement stmt) throws SQLException {
		int i = 1;
		for (final int t : this.reordering) {
			this.paramTypes[t].store(stmt, i, this.paramValues[t]);
			i += this.paramTypes[t].storeLength(i, this.paramValues[t]);
		}
	}

	@Override
	public boolean closeResultSet() {
		return false;
	}

}
