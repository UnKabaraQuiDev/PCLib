package lu.kbra.pclib.db.query;

import java.util.Arrays;
import java.util.Objects;

import lu.kbra.pclib.db.impl.SQLQueryable;

public class Join {

	public static enum Type {
		INNER, LEFT, RIGHT, FULL
	}

	private final Type type;
	private final SQLQueryable<?> queryable;
	private final String alias;
	private final String on;
	private final String[] columns;

	public Join(Type type, SQLQueryable<?> queryable, String alias, String on, String[] columns) {
		this.type = Objects.requireNonNull(type);
		this.queryable = Objects.requireNonNull(queryable);
		this.alias = alias;
		this.on = Objects.requireNonNull(on);
		this.columns = columns == null ? new String[0] : columns;
	}

	public Type getType() {
		return type;
	}

	public SQLQueryable<?> getQueryable() {
		return queryable;
	}

	public String getAlias() {
		return alias;
	}

	public String getOn() {
		return on;
	}

	public String[] getColumns() {
		return columns;
	}

	@Override
	public String toString() {
		return "Join@" + System.identityHashCode(this) + " [type=" + type + ", queryable=" + queryable + ", alias=" + alias + ", on=" + on
				+ ", columns=" + Arrays.toString(columns) + "]";
	}

}