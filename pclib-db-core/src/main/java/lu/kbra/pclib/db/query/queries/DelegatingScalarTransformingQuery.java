package lu.kbra.pclib.db.query.queries;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.exception.InternalDBException;
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
public class DelegatingScalarTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

	private final String sql;
	private final ColumnType<Object, ?>[] paramTypes;
	private final Object[] paramValues;
	private final Query.Type type;
	private final int[] reordering;
	private final ColumnType<B, ?> returnColumnType;
	private final Type returnType;

	private final BiFunction<Query.Type, Enumeration<?>, B> delegateFunction;

	@Override
	public String getPreparedQuerySQL(final SQLQueryable<T> table) {
		return this.sql;
	}

	@Override
	public B transform(final SQLQueryable<T> table, final ResultSet rs) throws SQLException {
		final Enumeration<?> enumeration = new Enumeration<Object>() {

			private boolean initialized;
			private boolean hasNext;

			private void init() {
				if (this.initialized) {
					return;
				}

				this.initialized = true;
				try {
					this.hasNext = rs.next();
				} catch (SQLException e) {
					throw new InternalDBException(table.getStructure(), e);
				}
			}

			@Override
			public boolean hasMoreElements() {
				this.init();
				return this.hasNext;
			}

			@Override
			public Object nextElement() {
				this.init();

				if (!this.hasNext) {
					throw new NoSuchElementException();
				}

				final Object value;
				try {
					value = DelegatingScalarTransformingQuery.this.returnColumnType
							.load(rs, 1, DelegatingScalarTransformingQuery.this.returnType);
				} catch (SQLException e) {
					throw new InternalDBException(table.getStructure(), e);
				}

				this.initialized = false;
				return value;
			}

		};

		return this.delegateFunction.apply(type, enumeration);
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
