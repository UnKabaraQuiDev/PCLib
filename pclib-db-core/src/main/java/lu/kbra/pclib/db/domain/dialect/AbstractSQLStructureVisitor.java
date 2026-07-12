package lu.kbra.pclib.db.domain.dialect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.UnionTableStructure;
import lu.kbra.pclib.db.domain.view.ViewColumnStructure;
import lu.kbra.pclib.db.domain.view.ViewCommonTableExpressionStructure;
import lu.kbra.pclib.db.domain.view.ViewOrderStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.domain.view.ViewTableStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public abstract class AbstractSQLStructureVisitor implements SQLStructureVisitor {

	private final Map<DbmsCapability, Boolean> capabilities = new EnumMap<>(DbmsCapability.class);
	private final Map<String, Object> options = new HashMap<>();

	protected AbstractSQLStructureVisitor() {
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String count(final B queryable) {
		return String.format("SELECT COUNT(*) AS %s FROM %s;", this.qualifiedName("count"), queryable.getStructure().getQualifiedName());
	}

	@Override
	public String[] create(final TableStructure table) {
		final StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(table.getQualifiedName());
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

		if (this.supports(DbmsCapability.TABLE_CHARACTER_SET) && table.hasHint(DefaultQueryableHints.CHARACTER_SET)) {
			sb.append(" CHARACTER SET ").append(table.<String>getHint(DefaultQueryableHints.CHARACTER_SET));
		}
		if (this.supports(DbmsCapability.TABLE_ENGINE) && table.hasHint(DefaultQueryableHints.ENGINE)) {
			sb.append(" ENGINE=").append(table.<String>getHint(DefaultQueryableHints.ENGINE));
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

		if (view.getWithTables().length != 0) {
			for (int i = 0; i < view.getWithTables().length; i++) {
				final ViewCommonTableExpressionStructure with = view.getWithTables()[i];
				sql.append(i == 0 ? "WITH " : ", ");
				sql.append(this.qualifiedName(with.getName())).append(" AS (\n").append(this.buildWithSQL(with)).append("\n)\n");
			}
		}

		sql.append(this.buildSelectBody(view)).append(";");
		return new String[] { this.drop(view), sql.toString() };
	}

	@Override
	public String drop(final DatabaseStructure databaseStructure) {
		return "DROP DATABASE IF EXISTS " + databaseStructure.getQualifiedName() + ";";
	}

	@Override
	public String drop(final TableStructure tableStructure) {
		return "DROP TABLE IF EXISTS " + tableStructure.getQualifiedName() + ";";
	}

	@Override
	public String drop(final ViewStructure tableStructure) {
		return "DROP VIEW IF EXISTS " + tableStructure.getQualifiedName() + ";";
	}

	@Override
	public Map<String, Object> getOptions() {
		return this.options;
	}

	@Override
	public String getQueryableName(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		final String name = (String) queryableHints.get(DefaultQueryableHints.NAME_OVERRIDE);
		return name == null || name.trim().isEmpty() ? PCUtils.camelCaseToSnakeCase(
				(tableClass.isAnonymousClass() ? tableClass.getSuperclass() : tableClass).getSimpleName().replaceAll("(Table|View)$", ""))
				: name;
	}

	@Override
	public String[] getQueryableNameParts(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		final String name = (String) queryableHints.get(DefaultQueryableHints.NAME_OVERRIDE);
		return new String[] {
				name == null || name.trim().isEmpty() ? PCUtils
						.camelCaseToSnakeCase((tableClass.isAnonymousClass() ? tableClass.getSuperclass() : tableClass).getSimpleName()
								.replaceAll("(Table|View)$", ""))
						: name };
	}

	@Override
	public <T extends DatabaseEntry> String getTruncateSQL(final AbstractDBTable<T> queryable) {
		return "TRUNCATE TABLE " + queryable.getStructure().getQualifiedName() + ";";
	}

	public boolean isRawExpression(final String identifier) {
		return identifier.indexOf(' ') >= 0 || identifier.indexOf('(') >= 0 || identifier.indexOf(')') >= 0 || identifier.indexOf(',') >= 0
				|| identifier.indexOf('\'') >= 0 || identifier.indexOf(';') >= 0;
	}

	@Override
	public String qualifiedName(final Class<? extends SQLQueryable<?>> tableClass, final Map<String, Object> queryableHints) {
		return this.qualifiedName(this.getQueryableNameParts(tableClass, queryableHints));
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
	public <B extends AbstractDBTable<T>, T extends DatabaseEntry> String safeDelete(final B table, final String[] pkNames) {
		return String.format("DELETE FROM %s WHERE %s;",
				table.getStructure().getQualifiedName(),
				Arrays.stream(pkNames).map(c -> this.qualifiedName(c) + " = ?").collect(Collectors.joining(" AND ")));
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DatabaseEntry> String safeInsert(final B table, final String[] columns) {
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
		return String.format("INSERT INTO %s (%s) VALUES (%s);",
				table.getStructure().getQualifiedName(),
				sbKeys.toString(),
				sbValues.toString());
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String safeSelect(final B table, final String[] whereColumns) {
		final StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(table.getStructure().getQualifiedName());

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
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String
			safeSelect(final B table, final String[] columns, final String[] whereColumns) {
		final StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(Arrays.stream(columns).map(this::qualifiedName).collect(Collectors.joining(", ")));
		sql.append(" FROM ");
		sql.append(table.getStructure().getQualifiedName());

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
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String
			safeSelect(final SQLQueryable<T> instance, final String[] whereColumns, final boolean limit, final boolean offset) {

		final StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(instance.getStructure().getQualifiedName());

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
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String safeSelect(
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

		sql.append(" FROM ").append(instance.getStructure().getQualifiedName());

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
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String
			safeSelectCountUniqueCollision(final B instance, final String[][] whereColumns) {
		return String.format("SELECT count(*) as %s FROM %s WHERE %s;",
				this.qualifiedName("count"),
				instance.getStructure().getQualifiedName(),
				Arrays.stream(whereColumns)
						.map(l -> Arrays.stream(l).map(i -> this.qualifiedName(i) + " = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR ")));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DatabaseEntry> String
			safeSelectUniqueCollision(final B instance, final String[][] uniqueKeys) {
		return String.format("SELECT * FROM %s WHERE %s;",
				instance.getStructure().getQualifiedName(),
				Arrays.stream(uniqueKeys)
						.map(l -> Arrays.stream(l).map(i -> this.qualifiedName(i) + " = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR ")));
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DatabaseEntry> String
			safeUpdate(final B table, final String[] setColumns, final String[] whereColumns) {
		return String.format("UPDATE %s SET %s WHERE %s;",
				table.getStructure().getQualifiedName(),
				Arrays.stream(setColumns).map(c -> this.qualifiedName(c) + "=?").collect(Collectors.joining(", ")),
				Arrays.stream(whereColumns).map(c -> this.qualifiedName(c) + "=?").collect(Collectors.joining(" AND ")));
	}

	protected void appendWhereGroupOrder(
			final StringBuilder sql,
			final String condition,
			final String[] groupBy,
			final ViewOrderStructure[] orderBy) {
		if (condition != null && !condition.trim().isEmpty()) {
			sql.append("\nWHERE \n\t").append(condition);
		}

		if (groupBy.length != 0) {
			sql.append("\nGROUP BY \n\t").append(Arrays.stream(groupBy).map(this::qualifiedName).collect(Collectors.joining(", ")));
		}

		if (orderBy.length != 0) {
			sql.append("\nORDER BY \n\t")
					.append(Arrays.stream(orderBy)
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
		return " AS " + column.getName();
	}

	protected String buildColumn(final TableStructure table, final ColumnData column, final boolean inlinePrimaryKey) {
		if (column.isGenerated()) {
			return this.buildGeneratedColumn(column);
		}

		final StringBuilder sb = new StringBuilder();
		final String typeSQL = inlinePrimaryKey && this.supports(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT) ? "INTEGER"
				: column.getType().build(this);
		sb.append(column.getLocalQualifiedName()).append(" ").append(typeSQL);

		if (inlinePrimaryKey) {
			sb.append(" PRIMARY KEY AUTOINCREMENT");
		} else if (column.isAutoIncrement() && this.supports(DbmsCapability.COLUMN_AUTO_INCREMENT)) {
			sb.append(" AUTO_INCREMENT");
		} else if (column.isAutoIncrement() && this.supports(DbmsCapability.INLINE_PRIMARY_KEY_AUTOINCREMENT)) {
			throw new IllegalArgumentException("Inline AUTOINCREMENT requires one INTEGER PRIMARY KEY column: " + column.getLocalName());
		}

		if (!column.isNullable() && !inlinePrimaryKey) {
			sb.append(" NOT NULL");
		}

		if (column.hasDefaultValue()) {
			sb.append(" DEFAULT ").append(column.<String>getHint(DefaultColumnHints.DEFAULT_VALUE));
		}

		if (this.supports(DbmsCapability.COLUMN_ON_UPDATE) && column.hasOnUpdate()) {
			sb.append(" ON UPDATE ").append(column.<String>getHint(DefaultColumnHints.ON_UPDATE));
		}

		return sb.toString();
	}

	protected String buildColumnSQL(final UnionTableStructure table, final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: table.getQualifiedName() + "." + ("*".equals(column.getName()) ? "*" : column.getName());

		return source + this.buildAlias(column);
	}

	protected String buildColumnSQL(final ViewColumnStructure column) {
		final String source = column.getName() == null ? column.getFunc()
				: "*".equals(column.getName()) ? "*"
				: column.getName();
		return source + this.buildAlias(column);
	}

	protected String buildColumnSQL(final ViewTableStructure table, final ViewColumnStructure column) {
		String source;
		if (column.getName() == null) {
			source = column.getFunc();
		} else {
			if (table.getAlias() != null) {
				source = table.getAlias();
			} else {
				source = table.getQualifiedName();
			}

			if ("*".equals(column.getName())) {
				source += ".*";
			} else {
				source += "." + this.qualifiedName(column.getName());
			}
		}

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

	protected String buildGeneratedColumn(final ColumnData column) {
		final StringBuilder sb = new StringBuilder();
		sb.append(column.getLocalQualifiedName()).append(" ").append(column.getType().build(this));
		sb.append(" GENERATED ALWAYS AS (")
				.append(column.<String>getHint(DefaultColumnHints.GENERATED_VALUE))
				.append(") ")
				.append(column.<Generated.Type>getHint(DefaultColumnHints.GENERATED_STORAGE_TYPE).name());
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

		sql.append(PCUtils.leftPadLine(Arrays.stream(view.getTables())
				.flatMap(t -> t.getColumns().stream().map(c -> this.buildColumnSQL(t, c)))
				.collect(Collectors.joining(", \n")), "\t")).append("\n");

		final ViewTableStructure mainTable = view.getMainTable();

		if (mainTable.getJoinType() == ViewTable.Type.MAIN_UNION || mainTable.getJoinType() == ViewTable.Type.MAIN_UNION_ALL) {
			sql.append("FROM (\n");
			sql.append(
					PCUtils.leftPadLine(
							Arrays.stream(view.getUnionTables())
									.map(this::buildUnionSQL)
									.collect(Collectors
											.joining(mainTable.getJoinType() == ViewTable.Type.MAIN_UNION ? "UNION \n" : "UNION ALL \n")),
							"\t"));
			sql.append("\n)");

			if (mainTable.getAlias() != null) {
				sql.append(" AS ").append(this.qualifiedName(mainTable.getAlias()));
			}
		} else {
			sql.append("FROM \n\t").append(mainTable.getQualifiedName());

			if (mainTable.getAlias() != null) {
				sql.append(" AS ").append(this.qualifiedName(mainTable.getAlias()));
			}

			for (final ViewTableStructure join : view.getJoinTables()) {
				sql.append("\n").append(this.joinKeyword(join.getJoinType())).append(" ").append(join.getQualifiedName());

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
				+ "\nFROM \n\t" + table.getQualifiedName() + "\n";
	}

	protected String buildWithSQL(final ViewCommonTableExpressionStructure with) {
		final StringBuilder sql = new StringBuilder();

		sql.append("SELECT\n");
		sql.append(
				PCUtils.leftPadLine(Arrays.stream(with.getColumns()).map(this::buildColumnSQL).collect(Collectors.joining(", \n")), "\t"))
				.append("\n");

		final ViewTableStructure mainTable = with.getMainTable();

		sql.append("FROM \n\t");
		sql.append(mainTable.getQualifiedName());

		if (mainTable.getAlias() != null) {
			sql.append(" AS ").append(this.qualifiedName(mainTable.getAlias()));
		}

		for (final ViewTableStructure join : with.getJoinTables()) {
			sql.append("\n").append(this.joinKeyword(join.getJoinType())).append(" ").append(join.getQualifiedName());

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

	protected String escapeList(final ColumnData[] columns) {
		return Arrays.stream(columns).map(ColumnData::getLocalQualifiedName).collect(Collectors.joining(", "));
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
					final ColumnData[] columns = ((PrimaryKeyData) constraint).getColumns();
					if (columns.length == 1 && columns[0].getLocalName().equals(col.getLocalName())) {
						return col;
					}
				}
			}
			throw new IllegalArgumentException("Inline AUTOINCREMENT requires one INTEGER PRIMARY KEY column: " + col.getLocalName());
		}

		return null;
	}

	protected final Map<DbmsCapability, Boolean> getCapabilities() {
		return new EnumMap<>(this.capabilities);
	}

	protected String joinKeyword(final ViewTable.Type joinType) {
		if (joinType == ViewTable.Type.MAIN || joinType == ViewTable.Type.MAIN_UNION || joinType == ViewTable.Type.MAIN_UNION_ALL) {
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
