package lu.kbra.pclib.db.autobuild.dialect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.column.GeneratedColumnData;
import lu.kbra.pclib.db.autobuild.table.CheckData;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.ForeignKeyData;
import lu.kbra.pclib.db.autobuild.table.PrimaryKeyData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.autobuild.table.UniqueData;
import lu.kbra.pclib.db.autobuild.view.UnionTableStructure;
import lu.kbra.pclib.db.autobuild.view.ViewColumnStructure;
import lu.kbra.pclib.db.autobuild.view.ViewCommonTableExpressionStructure;
import lu.kbra.pclib.db.autobuild.view.ViewJoinType;
import lu.kbra.pclib.db.autobuild.view.ViewOrderStructure;
import lu.kbra.pclib.db.autobuild.view.ViewStructure;
import lu.kbra.pclib.db.autobuild.view.ViewTableStructure;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public abstract class AbstractSQLStructureVisitor implements SQLStructureVisitor {

	protected final DataBaseConnector connector;

	protected AbstractSQLStructureVisitor(final DataBaseConnector connector) {
		this.connector = connector;
	}

	protected abstract String escapeStart();

	protected abstract String escapeEnd();

	protected boolean supportsTableCharacterSet() {
		return false;
	}

	protected boolean supportsTableEngine() {
		return false;
	}

	protected boolean supportsColumnOnUpdate() {
		return false;
	}

	protected boolean supportsColumnAutoIncrement() {
		return false;
	}

	protected boolean supportsColumnAutoincrementKeyword() {
		return false;
	}

	protected boolean qualifyCteTablesWithDatabase() {
		return false;
	}

	protected boolean isSQLite() {
		return false;
	}

	protected String escape(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Identifier cannot be null.");
		}
		if (value.startsWith(this.escapeStart()) && value.endsWith(this.escapeEnd())) {
			return value;
		}
		return this.escapeStart() + value.replace(this.escapeEnd(), this.escapeEnd() + this.escapeEnd()) + this.escapeEnd();
	}

	@Override
	public String visit(final TableStructure table) {
		final StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(this.escape(table.getName()));
		sb.append(" (\n");

		final List<String> definitions = new ArrayList<>();
		final ColumnData inlinePrimaryKey = this.findInlinePrimaryKey(table);

		for (final ColumnData col : table.getColumns()) {
			definitions.add("  " + this.buildColumn(table, col, col == inlinePrimaryKey));
		}

		if (table.getConstraints() != null) {
			for (final ConstraintData constraint : table.getConstraints()) {
				if (inlinePrimaryKey != null && constraint instanceof PrimaryKeyData) {
					continue;
				}
				definitions.add("  " + this.buildConstraint(constraint));
			}
		}

		sb.append(String.join(",\n", definitions));
		sb.append("\n)");

		if (this.supportsTableCharacterSet() && table.getCharacterSet() != null && !table.getCharacterSet().isEmpty()) {
			sb.append(" CHARACTER SET ").append(table.getCharacterSet());
		}

		if (this.supportsTableEngine() && table.getEngine() != null && !table.getEngine().isEmpty()) {
			sb.append(" ENGINE=").append(table.getEngine());
		}

		sb.append(";\n");
		return sb.toString();
	}

	protected ColumnData findInlinePrimaryKey(final TableStructure table) {
		if (!this.supportsColumnAutoincrementKeyword() || table.getColumns() == null || table.getConstraints() == null) {
			return null;
		}

		for (final ColumnData col : table.getColumns()) {
			if (!col.isAutoIncrement()) {
				continue;
			}
			for (final ConstraintData constraint : table.getConstraints()) {
				if (constraint instanceof PrimaryKeyData) {
					final String[] columns = ((PrimaryKeyData) constraint).getColumns();
					if (columns.length == 1 && columns[0].equals(col.getName())) {
						return col;
					}
				}
			}
			throw new IllegalArgumentException("SQLite AUTOINCREMENT requires one INTEGER PRIMARY KEY column: " + col.getName());
		}

		return null;
	}

	protected String buildColumn(final TableStructure table, final ColumnData column, final boolean inlinePrimaryKey) {
		if (column instanceof GeneratedColumnData) {
			return this.buildGeneratedColumn((GeneratedColumnData) column);
		}

		final StringBuilder sb = new StringBuilder();
		final String typeSQL = inlinePrimaryKey && this.supportsColumnAutoincrementKeyword() ? "INTEGER" : column.getType().build(this.connector);
		sb.append(this.escape(column.getName())).append(" ").append(typeSQL);

		if (inlinePrimaryKey) {
			sb.append(" PRIMARY KEY AUTOINCREMENT");
		} else if (column.isAutoIncrement() && this.supportsColumnAutoIncrement()) {
			sb.append(" AUTO_INCREMENT");
		} else if (column.isAutoIncrement() && this.supportsColumnAutoincrementKeyword()) {
			throw new IllegalArgumentException("SQLite AUTOINCREMENT requires one INTEGER PRIMARY KEY column: " + column.getName());
		}

		if (!column.isNullable() && !inlinePrimaryKey) {
			sb.append(" NOT NULL");
		}

		if (column.getDefaultValue() != null) {
			sb.append(" DEFAULT ").append(column.getDefaultValue());
		}

		if (this.supportsColumnOnUpdate() && column.getOnUpdate() != null) {
			sb.append(" ON UPDATE ").append(column.getOnUpdate());
		}

		return sb.toString();
	}

	protected String buildGeneratedColumn(final GeneratedColumnData column) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.escape(column.getName())).append(" ").append(column.getType().build(this.connector));
		sb.append(" GENERATED ALWAYS AS (").append(column.getDefaultValue()).append(") ").append(column.getStorageType().name());
		if (!column.isNullable() && !this.isSQLite()) {
			sb.append(" NOT NULL");
		}
		return sb.toString();
	}

	protected String buildConstraint(final ConstraintData constraint) {
		if (constraint instanceof PrimaryKeyData) {
			final PrimaryKeyData pk = (PrimaryKeyData) constraint;
			return "CONSTRAINT " + this.escape(pk.getName()) + " PRIMARY KEY (" + this.escapeList(pk.getColumns()) + ")";
		}
		if (constraint instanceof UniqueData) {
			final UniqueData unique = (UniqueData) constraint;
			return "CONSTRAINT " + this.escape(unique.getName()) + " UNIQUE (" + this.escapeList(unique.getColumns()) + ")";
		}
		if (constraint instanceof ForeignKeyData) {
			return this.buildForeignKey((ForeignKeyData) constraint);
		}
		if (constraint instanceof CheckData) {
			final CheckData check = (CheckData) constraint;
			return "CONSTRAINT " + this.escape(check.getName()) + " CHECK (" + check.getExpression() + ")";
		}
		return constraint.build(this.connector);
	}

	protected String buildForeignKey(final ForeignKeyData fk) {
		final StringBuilder sb = new StringBuilder();
		sb.append("CONSTRAINT ")
				.append(this.escape(fk.getName()))
				.append(" FOREIGN KEY (")
				.append(this.escapeList(fk.getColumns()))
				.append(") REFERENCES ")
				.append(this.escape(fk.getReferencedTable()))
				.append(" (")
				.append(this.escapeList(fk.getReferencedColumns()))
				.append(")");

		if (fk.getOnDeleteAction() != null) {
			sb.append(" ON DELETE ").append(fk.getOnDeleteAction());
		}

		if (fk.getOnUpdateAction() != null) {
			sb.append(" ON UPDATE ").append(fk.getOnUpdateAction());
		}

		return sb.toString();
	}

	protected String escapeList(final String[] columns) {
		return Arrays.stream(columns).map(this::escape).collect(Collectors.joining(", "));
	}

	@Override
	public String visit(final ViewStructure view) {
		if (view.getCustomSQL() != null && !view.getCustomSQL().trim().isEmpty()) {
			return view.getCustomSQL();
		}

		final StringBuilder sql = new StringBuilder();
		sql.append("CREATE VIEW ").append(this.escape(view.getName())).append(" AS \n");

		if (!view.getWithTables().isEmpty()) {
			for (int i = 0; i < view.getWithTables().size(); i++) {
				final ViewCommonTableExpressionStructure with = view.getWithTables().get(i);
				sql.append(i == 0 ? "WITH " : ", ");
				sql.append(this.escape(with.getName())).append(" AS (\n").append(this.buildWithSQL(with)).append("\n)\n");
			}
		}

		sql.append(this.buildSelectBody(view)).append(";");
		return sql.toString();
	}

	protected String buildSelectBody(final ViewStructure view) {
		final StringBuilder sql = new StringBuilder();

		sql.append("SELECT");
		if (view.isDistinct()) {
			sql.append(" DISTINCT");
		}
		sql.append("\n");

		sql.append(PCUtils.leftPadLine(view.getTables()
				.stream()
				.flatMap(t -> t.getColumns().stream().map(c -> this.buildColumnSQL(t, c)))
				.collect(Collectors.joining(", \n")), "\t")).append("\n");

		final ViewTableStructure mainTable = view.getMainTable();

		if (mainTable.getJoinType() == ViewJoinType.MAIN_UNION || mainTable.getJoinType() == ViewJoinType.MAIN_UNION_ALL) {
			sql.append("FROM (\n");
			sql.append(PCUtils.leftPadLine(view.getUnionTables()
					.stream()
					.map(this::buildUnionSQL)
					.collect(Collectors.joining(mainTable.getJoinType() == ViewJoinType.MAIN_UNION ? "UNION \n" : "UNION ALL \n")),
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

			for (final ViewTableStructure join : view.getJoinTables()) {
				sql.append("\n").append(this.joinKeyword(join.getJoinType())).append(" ").append(this.escape(join.getEffectiveName()));

				if (join.getAlias() != null) {
					sql.append(" AS ").append(this.escape(join.getAlias()));
				}

				sql.append(" ON ").append(join.getOn());
			}
		}

		this.appendWhereGroupOrder(sql, view.getCondition(), view.getGroupBy(), view.getOrderBy());

		return sql.toString();
	}

	protected String buildWithSQL(final ViewCommonTableExpressionStructure with) {
		final StringBuilder sql = new StringBuilder();

		sql.append("SELECT\n");
		sql.append(PCUtils.leftPadLine(with.getColumns().stream().map(this::buildColumnSQL).collect(Collectors.joining(", \n")), "\t"))
				.append("\n");

		final ViewTableStructure mainTable = with.getMainTable();

		sql.append("FROM \n\t");
		if (this.qualifyCteTablesWithDatabase() && this.connector.getDatabase() != null && !this.connector.getDatabase().isEmpty()) {
			sql.append(this.escape(this.connector.getDatabase())).append(".");
		}
		sql.append(this.escape(mainTable.getEffectiveName()));

		if (mainTable.getAlias() != null) {
			sql.append(" AS ").append(this.escape(mainTable.getAlias()));
		}

		for (final ViewTableStructure join : with.getJoinTables()) {
			sql.append("\n").append(this.joinKeyword(join.getJoinType())).append(" ").append(this.escape(join.getEffectiveName()));

			if (join.getAlias() != null) {
				sql.append(" AS ").append(this.escape(join.getAlias()));
			}

			sql.append(" ON ").append(join.getOn());
		}

		this.appendWhereGroupOrder(sql, with.getCondition(), with.getGroupBy(), with.getOrderBy());

		return sql.toString();
	}

	protected void appendWhereGroupOrder(
			final StringBuilder sql,
			final String condition,
			final List<String> groupBy,
			final List<ViewOrderStructure> orderBy) {
		if (condition != null && !condition.trim().isEmpty()) {
			sql.append("\nWHERE \n\t").append(condition);
		}

		if (!groupBy.isEmpty()) {
			sql.append("\nGROUP BY \n\t").append(groupBy.stream().map(this::escape).collect(Collectors.joining(", ")));
		}

		if (!orderBy.isEmpty()) {
			sql.append("\nORDER BY \n\t")
					.append(orderBy.stream().map(o -> this.escape(o.getColumn()) + " " + o.getType()).collect(Collectors.joining(", ")));
		}
	}

	protected String buildUnionSQL(final UnionTableStructure table) {
		return "SELECT \n\t" + table.getColumns().stream().map(c -> this.buildColumnSQL(table, c)).collect(Collectors.joining(", \n\t"))
				+ "\nFROM \n\t" + this.escape(table.getEffectiveName()) + "\n";
	}

	protected String buildColumnSQL(final ViewTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: this.escape(table.getAlias() != null ? table.getAlias() : table.getEffectiveName()) + "."
						+ ("*".equals(column.getName()) ? "*" : this.escape(column.getName()));

		return source + this.buildAlias(column);
	}

	protected String buildColumnSQL(final UnionTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: this.escape(table.getEffectiveName()) + "." + ("*".equals(column.getName()) ? "*" : this.escape(column.getName()));

		return source + this.buildAlias(column);
	}

	protected String buildColumnSQL(final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc() : "*".equals(column.getName()) ? "*" : this.escape(column.getName());
		return source + this.buildAlias(column);
	}

	protected String buildAlias(final ViewColumnStructure column) {
		if (column.getAlias() != null) {
			return " AS " + this.escape(column.getAlias());
		}
		if (column.getName() == null || "*".equals(column.getName())) {
			return "";
		}
		return " AS " + this.escape(column.getName());
	}

	protected String joinKeyword(final ViewJoinType joinType) {
		if (joinType == ViewJoinType.MAIN || joinType == ViewJoinType.MAIN_UNION || joinType == ViewJoinType.MAIN_UNION_ALL) {
			throw new IllegalArgumentException("Main join type cannot be used as a join table: " + joinType);
		}
		return joinType.name() + " JOIN";
	}

}
