package lu.kbra.pclib.db.autobuild.view;

import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.view.AbstractDBView;

public final class ViewSQLBuilder {

	private final String dbName;
	private final ViewStructure view;

	public ViewSQLBuilder(final AbstractDBView<?> dataBase, final ViewStructure view) {
		this.dbName = dataBase.getDataBase().getDataBaseName();
		this.view = view;
	}

	public ViewSQLBuilder(final DataBase db, final ViewStructure view) {
		this.dbName = db.getDataBaseName();
		this.view = view;
	}

	public ViewSQLBuilder(final String db, final ViewStructure view) {
		this.dbName = db;
		this.view = view;
	}

	public String buildCreateSQL() {
		if (this.view.getCustomSQL() != null && !this.view.getCustomSQL().trim().isEmpty()) {
			return this.view.getCustomSQL();
		}

		final StringBuilder sql = new StringBuilder();
		sql.append("CREATE VIEW ").append(this.escape(this.view.getName())).append(" AS \n");

		if (!this.view.getWithTables().isEmpty()) {
			for (int i = 0; i < this.view.getWithTables().size(); i++) {
				final ViewCommonTableExpressionStructure with = this.view.getWithTables().get(i);
				sql.append(i == 0 ? "WITH " : ", ");
				sql.append(this.escape(with.getName())).append(" AS (\n").append(this.buildWithSQL(with)).append("\n)\n");
			}
		}

		sql.append(this.buildSelectBody()).append(";");
		return sql.toString();
	}

	private String buildSelectBody() {
		final StringBuilder sql = new StringBuilder();

		sql.append("SELECT");
		if (this.view.isDistinct()) {
			sql.append(" DISTINCT");
		}
		sql.append("\n");

		sql.append(PCUtils.leftPadLine(this.view.getTables()
				.stream()
				.flatMap(t -> t.getColumns().stream().map(c -> this.buildColumnSQL(t, c)))
				.collect(Collectors.joining(", \n")), "\t")).append("\n");

		final ViewTableStructure mainTable = this.view.getMainTable();

		if (mainTable.getJoinType() == ViewJoinType.MAIN_UNION || mainTable.getJoinType() == ViewJoinType.MAIN_UNION_ALL) {

			sql.append("FROM (\n");
			sql.append(
					PCUtils.leftPadLine(
							this.view.getUnionTables()
									.stream()
									.map(this::buildUnionSQL)
									.collect(Collectors
											.joining(mainTable.getJoinType() == ViewJoinType.MAIN_UNION ? "UNION \n" : "UNION ALL \n")),
							"\t"));
			sql.append("\n)");

			if (mainTable.getAlias() != null) {
				sql.append(" AS ").append(this.escape(mainTable.getAlias()));
			}
		} else {
			sql.append("FROM \n\t").append(this.escape(mainTable.getEffectiveName()));

			if (mainTable.getAlias() != null) {
				sql.append(" AS ").append(this.escape(mainTable.getAlias()));
			}

			for (final ViewTableStructure join : this.view.getJoinTables()) {
				sql.append("\n").append(join.getJoinType().name()).append(" JOIN ").append(this.escape(join.getEffectiveName()));

				if (join.getAlias() != null) {
					sql.append(" AS ").append(this.escape(join.getAlias()));
				}

				sql.append(" ON ").append(join.getOn());
			}
		}

		if (this.view.getCondition() != null && !this.view.getCondition().trim().isEmpty()) {
			sql.append("\nWHERE \n\t").append(this.view.getCondition());
		}

		if (!this.view.getGroupBy().isEmpty()) {
			sql.append("\nGROUP BY \n\t").append(this.view.getGroupBy().stream().map(this::escape).collect(Collectors.joining(", ")));
		}

		if (!this.view.getOrderBy().isEmpty()) {
			sql.append("\nORDER BY \n\t")
					.append(this.view.getOrderBy()
							.stream()
							.map(o -> this.escape(o.getColumn()) + " " + o.getType())
							.collect(Collectors.joining(", ")));
		}

		return sql.toString();
	}

	private String buildWithSQL(final ViewCommonTableExpressionStructure with) {
		final StringBuilder sql = new StringBuilder();

		sql.append("SELECT\n");
		sql.append(PCUtils.leftPadLine(with.getColumns().stream().map(this::buildColumnSQL).collect(Collectors.joining(", \n")), "\t"))
				.append("\n");

		final ViewTableStructure mainTable = with.getMainTable();

		sql.append("FROM \n\t").append(this.escape(this.dbName)).append(".").append(this.escape(mainTable.getEffectiveName()));

		if (mainTable.getAlias() != null) {
			sql.append(" AS ").append(this.escape(mainTable.getAlias()));
		}

		for (final ViewTableStructure join : with.getJoinTables()) {
			sql.append("\n").append(join.getJoinType().name()).append(" JOIN ").append(this.escape(join.getEffectiveName()));

			if (join.getAlias() != null) {
				sql.append(" AS ").append(this.escape(join.getAlias()));
			}

			sql.append(" ON ").append(join.getOn());
		}

		if (with.getCondition() != null && !with.getCondition().trim().isEmpty()) {
			sql.append("\nWHERE \n\t").append(with.getCondition());
		}

		if (!with.getGroupBy().isEmpty()) {
			sql.append("\nGROUP BY \n\t").append(with.getGroupBy().stream().map(this::escape).collect(Collectors.joining(", ")));
		}

		if (!with.getOrderBy().isEmpty()) {
			sql.append("\nORDER BY \n\t")
					.append(with.getOrderBy()
							.stream()
							.map(o -> this.escape(o.getColumn()) + " " + o.getType())
							.collect(Collectors.joining(", ")));
		}

		return sql.toString();
	}

	private String buildUnionSQL(final UnionTableStructure table) {
		return "SELECT \n\t" + table.getColumns().stream().map(c -> this.buildColumnSQL(table, c)).collect(Collectors.joining(", \n\t"))
				+ "\nFROM \n\t" + this.escape(table.getEffectiveName()) + "\n";
	}

	private String buildColumnSQL(final ViewTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: this.escape(table.getAlias() != null ? table.getAlias() : table.getEffectiveName()) + "."
						+ ("*".equals(column.getName()) ? "*" : this.escape(column.getName()));

		return source + this.buildAlias(column);
	}

	private String buildColumnSQL(final UnionTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: this.escape(table.getEffectiveName()) + "." + ("*".equals(column.getName()) ? "*" : this.escape(column.getName()));

		return source + this.buildAlias(column);
	}

	private String buildColumnSQL(final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: "*".equals(column.getName()) ? "*"
				: this.escape(column.getName());

		return source + this.buildAlias(column);
	}

	private String buildAlias(final ViewColumnStructure column) {
		if (column.getAlias() != null) {
			return " AS " + this.escape(column.getAlias());
		}
		if (column.getName() == null || "*".equals(column.getName())) {
			return "";
		}
		return " AS " + this.escape(column.getName());
	}

	private String escape(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Identifier cannot be null.");
		}
		return value.startsWith("`") && value.endsWith("`") ? value : "`" + value + "`";
	}

	@Override
	public String toString() {
		return "ViewSQLBuilder@" + System.identityHashCode(this) + " [dbName=" + this.dbName + ", view=" + this.view + "]";
	}

}
