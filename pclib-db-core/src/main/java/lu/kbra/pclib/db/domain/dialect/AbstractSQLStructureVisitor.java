package lu.kbra.pclib.db.domain.dialect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.GeneratedColumnData;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.domain.view.UnionTableStructure;
import lu.kbra.pclib.db.domain.view.ViewColumnStructure;
import lu.kbra.pclib.db.domain.view.ViewCommonTableExpressionStructure;
import lu.kbra.pclib.db.domain.view.ViewJoinType;
import lu.kbra.pclib.db.domain.view.ViewOrderStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.domain.view.ViewTableStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public abstract class AbstractSQLStructureVisitor implements SQLStructureVisitor {

	private final Map<DbmsCapability, Boolean> capabilities = new EnumMap<>(DbmsCapability.class);
	private final Map<String, Object> options = new HashMap<>();

	protected AbstractSQLStructureVisitor() {
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String count(final B queryable) {
		return String.format("SELECT COUNT(*) AS %s FROM %s;", this.qualifiedName("count"), this.qualifiedName(queryable));
	}

	@Override
	public String[] create(final TableStructure table) {
		final StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(this.qualifiedStructureName(table));
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

		if (this.supports(DbmsCapability.TABLE_CHARACTER_SET) && table.hasHint(DefaultTableHints.CHARACTER_SET)) {
			sb.append(" CHARACTER SET ").append(table.<String>getHint(DefaultTableHints.CHARACTER_SET));
		}
		if (this.supports(DbmsCapability.TABLE_ENGINE) && table.hasHint(DefaultTableHints.ENGINE)) {
			sb.append(" ENGINE=").append(table.<String>getHint(DefaultTableHints.ENGINE));
		}

		sb.append(";\n");
		return new String[] { sb.toString() };
	}

	@Override
	public String create(final TableStructure table, final ColumnData column) {
		return this.buildColumn(table, column, this.findInlinePrimaryKey(table) == column);
	}

	@Override
	public String[] create(final ViewStructure view) {
		if (view.getCustomSQL() != null && !view.getCustomSQL().trim().isEmpty()) {
			return new String[] { view.getCustomSQL() };
		}

		final StringBuilder sql = new StringBuilder();
		sql.append("CREATE VIEW ").append(this.qualifiedStructureName(view)).append(" AS \n");

		if (!view.getWithTables().isEmpty()) {
			for (int i = 0; i < view.getWithTables().size(); i++) {
				final ViewCommonTableExpressionStructure with = view.getWithTables().get(i);
				sql.append(i == 0 ? "WITH " : ", ");
				sql.append(this.qualifiedName(with.getName())).append(" AS (\n").append(this.buildWithSQL(with)).append("\n)\n");
			}
		}

		sql.append(this.buildSelectBody(view)).append(";");
		return new String[] { this.drop(view), sql.toString() };
	}

	@Override
	public String drop(final DataBaseStructure dataBaseStructure) {
		return "DROP DATABASE IF EXISTS " + this.qualifiedName(dataBaseStructure.getName()) + ";";
	}

	@Override
	public String drop(final TableStructure tableStructure) {
		return "DROP TABLE IF EXISTS " + this.qualifiedName(tableStructure.getName()) + ";";
	}

	@Override
	public String drop(final ViewStructure tableStructure) {
		return "DROP VIEW IF EXISTS " + this.qualifiedName(tableStructure.getName()) + ";";
	}

	@Override
	public Map<String, Object> getOptions() {
		return this.options;
	}

	@Override
	public String getQueryableName(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		final String name = (String) queryableHints.get(DefaultTableHints.NAME_OVERRIDE);
		return name == null || name.trim().isEmpty()
				? PCUtils.camelCaseToSnakeCase(tableClass.getSimpleName().replaceAll("(Table|View)$", ""))
				: name;
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String getTruncateSQL(final B queryable) {
		return "TRUNCATE TABLE " + this.qualifiedName(queryable) + ";";
	}

	public boolean isRawExpression(final String identifier) {
		return identifier.indexOf(' ') >= 0 || identifier.indexOf('(') >= 0 || identifier.indexOf(')') >= 0 || identifier.indexOf(',') >= 0
				|| identifier.indexOf('\'') >= 0 || identifier.indexOf(';') >= 0;
	}

	@Override
	public String qualifiedName(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		return this.qualifiedName(this.getQueryableName(tableClass, queryableHints));
	}

	@Override
	public String qualifiedName(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Identifier cannot be null.");
		}
		final String trimmed = value.trim();
		if (trimmed.startsWith(this.escapeStart()) && trimmed.endsWith(this.escapeEnd()) || trimmed.isEmpty() || "*".equals(trimmed)
				|| this.isRawExpression(trimmed)) {
			return trimmed;
		}
		return this.escapeStart() + value.replace(this.escapeEnd(), this.escapeEnd() + this.escapeEnd()) + this.escapeEnd();
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String safeDelete(final B table, final String[] pkNames) {
		return String.format("DELETE FROM %s WHERE %s;",
				this.qualifiedName(table),
				Arrays.stream(pkNames).map(c -> this.qualifiedName(c) + "=?").collect(Collectors.joining(" AND ")));
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String safeInsert(final B table, final String[] columns) {
		final StringBuilder sbKeys = new StringBuilder();
		final StringBuilder sbValues = new StringBuilder(3 * columns.length);
		for (int i = 0; i < columns.length; i++) {
			final String column = columns[i];
			if (i == columns.length - 1) {
				sbKeys.append(this.qualifiedName(column));
				sbValues.append("?");
			} else {
				sbKeys.append(this.qualifiedName(column)).append(", ");
				sbValues.append("?, ");
			}
		}
		return String.format("INSERT INTO %s (%s) VALUES (%s);", this.qualifiedName(table), sbKeys.toString(), sbValues.toString());
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String safeSelect(final B table, final String[] whereColumns) {
		final StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(this.qualifiedName(table));

		if (whereColumns != null && whereColumns.length != 0) {
			final String whereClause = Arrays.stream(whereColumns)
					.map(column -> this.qualifiedName(column) + " = ?")
					.collect(Collectors.joining(" AND "));

			sql.append(" WHERE ").append(whereClause);
		}

		sql.append(';');
		return sql.toString();
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			safeSelect(final B table, final String[] columns, final String[] whereColumns) {
		final StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(Arrays.stream(columns).map(this::qualifiedName).collect(Collectors.joining(", ")));
		sql.append(" FROM ");
		sql.append(this.qualifiedName(table));

		if (whereColumns != null && whereColumns.length != 0) {
			final String whereClause = Arrays.stream(whereColumns)
					.map(column -> this.qualifiedName(column) + " = ?")
					.collect(Collectors.joining(" AND "));

			sql.append(" WHERE ").append(whereClause);
		}

		sql.append(';');
		return sql.toString();
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			safeSelect(final SQLQueryable<T> instance, final String[] whereColumns, final boolean limit, final boolean offset) {

		final StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(this.qualifiedName(instance));

		boolean firstWhere = true;
		if (whereColumns != null) {
			for (final String column : whereColumns) {
				if (column == null) {
					continue;
				}

				if (firstWhere) {
					sql.append(" WHERE ");
					firstWhere = false;
				} else {
					sql.append(" AND ");
				}

				sql.append(this.qualifiedName(column)).append(" = ?");
			}
		}

		if (limit) {
			sql.append(" LIMIT ?");
		}

		if (offset) {
			sql.append(" OFFSET ?");
		}

		sql.append(';');
		return sql.toString();
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String safeSelect(
			final SQLQueryable<T> instance,
			final String[] columns,
			final String[] whereColumns,
			final boolean limit,
			final boolean offset) {

		final StringBuilder sql = new StringBuilder("SELECT ");

		boolean firstColumn = true;
		if (columns != null && columns.length > 0) {
			for (final String column : columns) {
				if (column == null) {
					continue;
				}

				if (!firstColumn) {
					sql.append(", ");
				}

				sql.append(this.qualifiedName(column));
				firstColumn = false;
			}
		}

		if (firstColumn) {
			sql.append('*');
		}

		sql.append(" FROM ").append(this.qualifiedName(instance));

		boolean firstWhere = true;
		if (whereColumns != null) {
			for (final String column : whereColumns) {
				if (column == null) {
					continue;
				}

				if (firstWhere) {
					sql.append(" WHERE ");
					firstWhere = false;
				} else {
					sql.append(" AND ");
				}

				sql.append(this.qualifiedName(column)).append(" = ?");
			}
		}

		if (limit) {
			sql.append(" LIMIT ?");
		}

		if (offset) {
			sql.append(" OFFSET ?");
		}

		sql.append(';');
		return sql.toString();
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			safeSelectCountUniqueCollision(final B instance, final String[][] whereColumns) {
		return String.format("SELECT count(*) as %s FROM %s WHERE %s;",
				this.qualifiedName("count"),
				this.qualifiedName(instance),
				Arrays.stream(whereColumns)
						.map(l -> Arrays.stream(l).map(i -> this.qualifiedName(i) + " = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR ")));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			safeSelectUniqueCollision(final B instance, final String[][] uniqueKeys) {
		return String.format("SELECT * FROM %s WHERE %s;",
				this.qualifiedName(instance),
				Arrays.stream(uniqueKeys)
						.map(l -> Arrays.stream(l).map(i -> this.qualifiedName(i) + " = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR ")));
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String
			safeUpdate(final B table, final String[] setColumns, final String[] whereColumns) {
		return String.format("UPDATE %s SET %s WHERE %s;",
				this.qualifiedName(table),
				Arrays.stream(setColumns).map(c -> this.qualifiedName(c) + "=?").collect(Collectors.joining(", ")),
				Arrays.stream(whereColumns).map(c -> this.qualifiedName(c) + "=?").collect(Collectors.joining(" AND ")));
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
			sql.append("\nGROUP BY \n\t").append(groupBy.stream().map(this::qualifiedName).collect(Collectors.joining(", ")));
		}

		if (!orderBy.isEmpty()) {
			sql.append("\nORDER BY \n\t")
					.append(orderBy.stream()
							.map(o -> this.qualifiedName(o.getColumn()) + " " + o.getType())
							.collect(Collectors.joining(", ")));
		}
	}

	protected String buildAlias(final ViewColumnStructure column) {
		if (column.getAlias() != null) {
			return " AS " + this.qualifiedName(column.getAlias());
		}
		if (column.getName() == null || "*".equals(column.getName())) {
			return "";
		}
		return " AS " + this.qualifiedName(column.getName());
	}

	protected String buildColumn(final TableStructure table, final ColumnData column, final boolean inlinePrimaryKey) {
		if (column instanceof GeneratedColumnData) {
			return this.buildGeneratedColumn((GeneratedColumnData) column);
		}

		final StringBuilder sb = new StringBuilder();
		final String typeSQL = inlinePrimaryKey && this.supports(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT) ? "INTEGER"
				: column.getType().build(this);
		sb.append(this.qualifiedName(column.getName())).append(" ").append(typeSQL);

		if (inlinePrimaryKey) {
			sb.append(" PRIMARY KEY AUTOINCREMENT");
		} else if (column.isAutoIncrement() && this.supports(DbmsCapability.COLUMN_AUTO_INCREMENT)) {
			sb.append(" AUTO_INCREMENT");
		} else if (column.isAutoIncrement() && this.supports(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT)) {
			throw new IllegalArgumentException("Inline AUTOINCREMENT requires one INTEGER PRIMARY KEY column: " + column.getName());
		}

		if (!column.isNullable() && !inlinePrimaryKey) {
			sb.append(" NOT NULL");
		}

		if (column.getDefaultValue() != null) {
			sb.append(" DEFAULT ").append(column.getDefaultValue());
		}

		if (this.supports(DbmsCapability.COLUMN_ON_UPDATE) && column.getOnUpdate() != null) {
			sb.append(" ON UPDATE ").append(column.getOnUpdate());
		}

		return sb.toString();
	}

	protected String buildColumnSQL(final UnionTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: this.qualifiedName(table.getEffectiveName()) + "."
						+ ("*".equals(column.getName()) ? "*" : this.qualifiedName(column.getName()));

		return source + this.buildAlias(column);
	}

	protected String buildColumnSQL(final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: "*".equals(column.getName()) ? "*"
				: this.qualifiedName(column.getName());
		return source + this.buildAlias(column);
	}

	protected String buildColumnSQL(final ViewTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: this.qualifiedName(table.getAlias() != null ? table.getAlias() : table.getEffectiveName()) + "."
						+ ("*".equals(column.getName()) ? "*" : this.qualifiedName(column.getName()));

		return source + this.buildAlias(column);
	}

	protected String buildConstraint(final ConstraintData constraint) {
		if (constraint instanceof PrimaryKeyData) {
			final PrimaryKeyData pk = (PrimaryKeyData) constraint;
			return "CONSTRAINT " + this.qualifiedName(pk.getName()) + " PRIMARY KEY (" + this.escapeList(pk.getColumns()) + ")";
		}
		if (constraint instanceof UniqueData) {
			final UniqueData unique = (UniqueData) constraint;
			return "CONSTRAINT " + this.qualifiedName(unique.getName()) + " UNIQUE (" + this.escapeList(unique.getColumns()) + ")";
		}
		if (constraint instanceof ForeignKeyData) {
			return this.buildForeignKey((ForeignKeyData) constraint);
		}
		if (constraint instanceof CheckData) {
			final CheckData check = (CheckData) constraint;
			return "CONSTRAINT " + this.qualifiedName(check.getName()) + " CHECK (" + check.getExpression() + ")";
		}
		throw new UnsupportedOperationException("Unknown constraint type: " + constraint.getClass().getName());
	}

	protected String buildForeignKey(final ForeignKeyData fk) {
		final StringBuilder sb = new StringBuilder();
		sb.append("CONSTRAINT ")
				.append(this.qualifiedName(fk.getName()))
				.append(" FOREIGN KEY (")
				.append(this.escapeList(fk.getColumns()))
				.append(") REFERENCES ")
				.append(this.qualifiedName(fk.getReferencedTable()))
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

	protected String buildGeneratedColumn(final GeneratedColumnData column) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.qualifiedName(column.getName())).append(" ").append(column.getType().build(this));
		sb.append(" GENERATED ALWAYS AS (").append(column.getDefaultValue()).append(") ").append(column.getStorageType().name());
		if (!column.isNullable() && this.supports(DbmsCapability.GENERATED_COLUMN_NOT_NULL)) {
			sb.append(" NOT NULL");
		}
		return sb.toString();
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
			sql.append(
					PCUtils.leftPadLine(
							view.getUnionTables()
									.stream()
									.map(this::buildUnionSQL)
									.collect(Collectors
											.joining(mainTable.getJoinType() == ViewJoinType.MAIN_UNION ? "UNION \n" : "UNION ALL \n")),
							"\t"));
			sql.append("\n)");

			if (mainTable.getAlias() != null) {
				sql.append(" AS ").append(this.qualifiedName(mainTable.getAlias()));
			}
		} else {
			sql.append("FROM \n\t").append(this.qualifiedName(mainTable.getEffectiveName()));

			if (mainTable.getAlias() != null) {
				sql.append(" AS ").append(this.qualifiedName(mainTable.getAlias()));
			}

			for (final ViewTableStructure join : view.getJoinTables()) {
				sql.append("\n")
						.append(this.joinKeyword(join.getJoinType()))
						.append(" ")
						.append(this.qualifiedName(join.getEffectiveName()));

				if (join.getAlias() != null) {
					sql.append(" AS ").append(this.qualifiedName(join.getAlias()));
				}

				sql.append(" ON ").append(join.getOn());
			}
		}

		this.appendWhereGroupOrder(sql, view.getCondition(), view.getGroupBy(), view.getOrderBy());

		return sql.toString();
	}

	protected String buildUnionSQL(final UnionTableStructure table) {
		return "SELECT \n\t" + table.getColumns().stream().map(c -> this.buildColumnSQL(table, c)).collect(Collectors.joining(", \n\t"))
				+ "\nFROM \n\t" + this.qualifiedName(table.getEffectiveName()) + "\n";
	}

	protected String buildWithSQL(final ViewCommonTableExpressionStructure with) {
		final StringBuilder sql = new StringBuilder();

		sql.append("SELECT\n");
		sql.append(PCUtils.leftPadLine(with.getColumns().stream().map(this::buildColumnSQL).collect(Collectors.joining(", \n")), "\t"))
				.append("\n");

		final ViewTableStructure mainTable = with.getMainTable();

		sql.append("FROM \n\t");
//		if (this.supports(DbmsCapability.QUALIFY_CTE_TABLES_WITH_DATABASE) && this.connector.getDatabase() != null
//				&& !this.connector.getDatabase().isEmpty()) {
//			sql.append(this.qualifiedName(this.connector.getDatabase())).append(".");
//		}
		sql.append(this.qualifiedName(mainTable.getEffectiveName()));

		if (mainTable.getAlias() != null) {
			sql.append(" AS ").append(this.qualifiedName(mainTable.getAlias()));
		}

		for (final ViewTableStructure join : with.getJoinTables()) {
			sql.append("\n").append(this.joinKeyword(join.getJoinType())).append(" ").append(this.qualifiedName(join.getEffectiveName()));

			if (join.getAlias() != null) {
				sql.append(" AS ").append(this.qualifiedName(join.getAlias()));
			}

			sql.append(" ON ").append(join.getOn());
		}

		this.appendWhereGroupOrder(sql, with.getCondition(), with.getGroupBy(), with.getOrderBy());

		return sql.toString();
	}

	protected abstract String escapeEnd();

	protected String escapeList(final String[] columns) {
		return Arrays.stream(columns).map(this::qualifiedName).collect(Collectors.joining(", "));
	}

	protected abstract String escapeStart();

	protected ColumnData findInlinePrimaryKey(final TableStructure table) {
		if (!this.supports(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT) || table.getColumns() == null
				|| table.getConstraints() == null) {
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
			throw new IllegalArgumentException("Inline AUTOINCREMENT requires one INTEGER PRIMARY KEY column: " + col.getName());
		}

		return null;
	}

	protected final Map<DbmsCapability, Boolean> getCapabilities() {
		return new EnumMap<>(this.capabilities);
	}

	protected String joinKeyword(final ViewJoinType joinType) {
		if (joinType == ViewJoinType.MAIN || joinType == ViewJoinType.MAIN_UNION || joinType == ViewJoinType.MAIN_UNION_ALL) {
			throw new IllegalArgumentException("Main join type cannot be used as a join table: " + joinType);
		}
		return joinType.name() + " JOIN";
	}

	protected String qualifiedStructureName(final TableStructure table) {
		return this.qualifiedName(table.getName());
	}

	protected String qualifiedStructureName(final ViewStructure view) {
		return this.qualifiedName(view.getName());
	}

	protected final void setCapability(final DbmsCapability capability, final boolean supported) {
		this.capabilities.put(capability, supported);
	}

	protected final boolean supports(final DbmsCapability capability) {
		return Boolean.TRUE.equals(this.capabilities.get(capability));
	}

}
