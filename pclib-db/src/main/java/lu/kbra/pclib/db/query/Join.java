package lu.kbra.pclib.db.query;

import java.util.Arrays;
import java.util.Objects;

import lu.kbra.pclib.db.impl.SQLQueryable;

public class Join {

	public enum Type {
		INNER,
		LEFT,
		RIGHT,
		FULL
	}

	private final Type type;
	private final SQLQueryable<?> queryable;
	private final String alias;
	private final String on;
	private final String[] columns;

	public Join(final Type type, final SQLQueryable<?> queryable, final String alias, final String on, final String[] columns) {
		this.type = Objects.requireNonNull(type);
		this.queryable = Objects.requireNonNull(queryable);
		this.alias = alias;
		this.on = Objects.requireNonNull(on);
		this.columns = columns == null ? new String[0] : columns;
	}

	public Type getType() {
		return this.type;
	}

	public SQLQueryable<?> getQueryable() {
		return this.queryable;
	}

	public String getAlias() {
		return this.alias;
	}

	public String getOn() {
		return this.on;
	}

	public String[] getColumns() {
		return this.columns;
	}

	@Override
	public String toString() {
		return "Join@" + System.identityHashCode(this) + " [type=" + this.type + ", queryable=" + this.queryable + ", alias=" + this.alias
				+ ", on=" + this.on + ", columns=" + Arrays.toString(this.columns) + "]";
	}

}
