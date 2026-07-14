package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tree.dependency.DependencyResolver;
import lu.kbra.pclib.datastructure.tree.dependency.DependencyTree;
import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyTriplet;
import lu.kbra.pclib.datastructure.tuple.Triplet;
import lu.kbra.pclib.datastructure.tuple.Triplets;
import lu.kbra.pclib.db.annotations.entry.Check;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.UnionTableStructure;
import lu.kbra.pclib.db.domain.view.ViewColumnStructure;
import lu.kbra.pclib.db.domain.view.ViewCommonTableExpressionStructure;
import lu.kbra.pclib.db.domain.view.ViewOrderStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.domain.view.ViewTableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner.SQLQueryableDependency;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtilsOptionsOwner;
import lu.kbra.pclib.db.view.AbstractDBView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class DatabaseScanner {

	@Data
	private static final class JoinPath {

		private final String leftAlias;
		private final String[] leftColumns;
		private final String rightAlias;
		private final String[] rightColumns;

		private JoinPath(final String leftAlias, final String[] leftColumns, final String rightAlias, final String[] rightColumns) {
			this.leftAlias = leftAlias;
			this.leftColumns = leftColumns;
			this.rightAlias = rightAlias;
			this.rightColumns = rightColumns;
		}

		@Override
		public String toString() {
			return this.toOnClause();
		}

		private String toOnClause() {
			if (this.leftColumns.length != this.rightColumns.length) {
				throw new IllegalStateException("Mismatched join column count.");
			}

			final List<String> parts = new ArrayList<>();
			for (int i = 0; i < this.leftColumns.length; i++) {
				parts.add(this.leftAlias + "." + this.leftColumns[i] + " = " + this.rightAlias + "." + this.rightColumns[i]);
			}
			return String.join(" AND ", parts);
		}

	}

	protected final Database database;
	protected final DatabaseEntryUtils databaseEntryUtils;
	protected final SQLStructureVisitor structureVisitor;
	protected final List<ReadOnlyTriplet<SQLQueryable<?>, Optional<Map<String, Object>>, Optional<Map<String, Object>>>> forScan = new ArrayList<>();
	protected final SQLFunctionResolver functionResolver;
	protected final Map<Class<? extends SQLQueryable<?>>, List<SQLQueryableStructure>> scanned = new HashMap<>();
	protected final Map<String, Object> baseHints;
	protected final HintScanner hintScanner;
	protected DependencyTree<? extends SQLQueryable<?>, SQLQueryableDependency> dependencyTree;

	public DatabaseScanner(final Database database) {
		this(database, null);
	}

	public DatabaseScanner(final Database database, final Map<String, Object> hints) {
		this.database = database;
		this.baseHints = hints == null ? new HashMap<>() : hints;
		if (database.getCustomHints() != null) {
			this.baseHints.putAll(database.getCustomHints());
		}
		this.databaseEntryUtils = database.getDatabaseEntryUtils();
		this.structureVisitor = this.databaseEntryUtils.getStructureVisitor();
		this.hintScanner = this.databaseEntryUtils.getHintScanner();
		this.functionResolver = this.databaseEntryUtils.getFunctionResolver();
	}

	public <B extends SQLQueryable<T>, T extends DatabaseEntry> DatabaseScanner register(final B instance) {
		this.forScan.add(Triplets.readOnly(instance, Optional.empty(), Optional.empty()));
		return this;
	}

	public <B extends SQLQueryable<T>, T extends DatabaseEntry> DatabaseScanner
			register(final B instance, final Map<String, Object> queryableHints, final Map<String, Object> entryHints) {
		this.forScan.add(Triplets.readOnly(instance, Optional.ofNullable(queryableHints), Optional.ofNullable(entryHints)));
		return this;
	}

	public void doScan() {
		final DatabaseStructure structure;
		if (this.database.getStructure() == null) {
			final String name = (String) this.baseHints.computeIfAbsent(DefaultQueryableHints.NAME_OVERRIDE, k -> {
				throw new IllegalStateException("Database has no name.");
			});
			final String queryableName = this.structureVisitor.qualifiedName(name);
			structure = new DatabaseStructure(name, queryableName, this.baseHints, null);
			this.database.setDatabaseStructure(structure);
		} else {
			structure = this.database.getStructure();
		}

		this.scanSelfStructure();
		this.scanLinks();

		this.dependencyTree = new DependencyResolver<>(this.forScan.stream().map(Triplet::getFirst).collect(Collectors.toList()),
				c -> c.getStructure().getDependencies(),
				c -> c.getStructure().getKey()).getTree();

		this.scanned.values().forEach(t -> t.forEach(q -> {
			if (q instanceof TableStructure) {
				structure.getTableStructures().add((TableStructure) q);
			} else if (q instanceof ViewStructure) {
				structure.getViewStructures().add((ViewStructure) q);
			} else {
				throw new IllegalArgumentException("Unknown DBStructure type: " + t.getClass());
			}
		}));

		structure.setDependencyTree(this.dependencyTree);

		this.forScan.clear();
		this.scanned.clear();
	}

	protected void scanSelfStructure() {
		for (final ReadOnlyTriplet<SQLQueryable<?>, Optional<Map<String, Object>>, Optional<Map<String, Object>>> pair : this.forScan) {
			final SQLQueryable<?> instance = pair.getFirst();
			final Map<String, Object> customQueryableHints = pair.getSecond().orElse(null);
			final Map<String, Object> customEntryHints = pair.getThird().orElse(null);
			final Class<? extends SQLQueryable<?>> tableClazz = (Class<? extends SQLQueryable<?>>) pair.getSecond()
					.map(c -> c.get(DefaultQueryableHints.TARGET_CLASS))
					.orElse(instance.getClass());

			if (instance instanceof AbstractDBTable<?>) {

				final TableStructure tableStructure = this.scanSelfTableStructure((AbstractDBTable<?>) instance,
						customQueryableHints,
						(Class<? extends AbstractDBTable<?>>) pair.getSecond()
								.map(c -> c.get(DefaultQueryableHints.TARGET_CLASS))
								.orElse(tableClazz.asSubclass(AbstractDBTable.class)),
						customEntryHints);
				((AbstractDBTable<?>) instance).setTableStructure(tableStructure);
				this.scanned.computeIfAbsent(tableClazz, k -> new ArrayList<>(1)).add(tableStructure);

			} else if (instance instanceof AbstractDBView<?>) {

				final ViewStructure viewStructure = this.scanSelfViewStructure((AbstractDBView<?>) instance,
						customQueryableHints,
						(Class<? extends AbstractDBView<?>>) pair.getSecond()
								.map(c -> c.get(DefaultQueryableHints.TARGET_CLASS))
								.orElse(tableClazz.asSubclass(AbstractDBView.class)),
						customEntryHints);
				((AbstractDBView<?>) instance).setViewStructure(viewStructure);
				this.scanned.computeIfAbsent(tableClazz, k -> new ArrayList<>(1)).add(viewStructure);

			} else {
				throw new IllegalArgumentException("Unknown SQLQueryable type: " + instance);
			}
		}
	}

	protected void scanLinks() {
		for (final ReadOnlyTriplet<SQLQueryable<?>, Optional<Map<String, Object>>, Optional<Map<String, Object>>> triplet : this.forScan) {
			final SQLQueryable<?> instance = triplet.getFirst();

			if (instance instanceof AbstractDBTable<?>) {
				this.scanTableLinks((AbstractDBTable<?>) instance);
			} else if (instance instanceof AbstractDBView<?>) {
				this.scanViewLinks((AbstractDBView<?>) instance);
			} else {
				throw new IllegalArgumentException("Unknown SQLQueryable type: " + instance);
			}
		}

		this.resolveMissingJoinConditions(this.forScan.stream()
				.map(Triplet::getFirst)
				.filter(AbstractDBView.class::isInstance)
				.map(AbstractDBView.class::cast)
				.map(AbstractDBView::getStructure)
				.flatMap(c -> Arrays.stream(c.getTables()))
				.collect(Collectors.toList()));
	}

	private void resolveMissingJoinConditions(final List<ViewTableStructure> tables) {
		final List<ViewTableStructure> resolved = new ArrayList<>();

		for (final ViewTableStructure table : tables) {
			if (table.getJoinType() == ViewTable.Type.MAIN || table.getJoinType() == ViewTable.Type.MAIN_UNION
					|| table.getJoinType() == ViewTable.Type.MAIN_UNION_ALL) {
				resolved.add(table);
				continue;
			}

			if (table.getOn() == null || table.getOn().trim().isEmpty()) {
				table.setOn(this.resolveJoinCondition(table, resolved));
			}

			resolved.add(table);
		}
	}

	private String resolveJoinCondition(final ViewTableStructure joinTable, final List<ViewTableStructure> candidates) {
		final List<JoinPath> matches = new ArrayList<>();

		for (final ViewTableStructure candidate : candidates) {
			matches.addAll(this.findJoinPaths(candidate, joinTable));
		}

		if (matches.isEmpty()) {
			throw new IllegalArgumentException("Could not resolve join condition for table '" + joinTable.getForeignName()
					+ "'. No foreign key path found to previously declared tables.");
		}

		if (matches.size() > 1) {
			throw new IllegalArgumentException("Could not resolve join condition for table '" + joinTable.getForeignName()
					+ "'. Multiple join paths found: " + matches.stream().map(JoinPath::toString).collect(Collectors.joining(", "))
					+ ". Please specify 'on' explicitly.");
		}

		return matches.get(0).toOnClause();
	}

	private List<JoinPath> findJoinPaths(final ViewTableStructure left, final ViewTableStructure right) {
		final List<JoinPath> paths = new ArrayList<>();

		Objects.requireNonNull(left.getForeignClass(),
				"Left ViewTable has no class type, cannot check for foreign constraints (" + left.getForeignName() + ", "
						+ left.getResolvedName().getName() + ").");
		Objects.requireNonNull(right.getForeignClass(),
				"Right ViewTable has no class type, cannot check for foreign constraints (" + right.getForeignName() + ", "
						+ right.getResolvedName().getName() + ").");

		final SQLQueryableStructure leftStructure = this.getStructureFor(left.getForeignClass(), left.getForeignName());
		final SQLQueryableStructure rightStructure = this.getStructureFor(right.getForeignClass(), right.getForeignName());

		if (leftStructure == null || rightStructure == null) {
			return paths;
		}

		final String leftTableName = leftStructure.getName();
		final String rightTableName = rightStructure.getName();

		final String leftAlias = left.getAlias() == null || left.getAlias().trim().isEmpty() ? left.getResolvedName().getName()
				: left.getAlias();
		final String rightAlias = right.getAlias() == null || right.getAlias().trim().isEmpty() ? right.getResolvedName().getName()
				: right.getAlias();

		// left has FK to right
		for (final ForeignKeyData fk : this.getForeignKeys(leftStructure)) {
			if (rightTableName.equals(fk.getReferencedTable())) {
				paths.add(new JoinPath(leftAlias, fk.getColumns(), rightAlias, fk.getReferencedColumns()));
			}
		}

		// right has FK to left
		for (final ForeignKeyData fk : this.getForeignKeys(rightStructure)) {
			if (leftTableName.equals(fk.getReferencedTable())) {
				paths.add(new JoinPath(leftAlias, fk.getReferencedColumns(), rightAlias, fk.getColumns()));
			}
		}

		return paths;
	}

	private List<ForeignKeyData> getForeignKeys(final SQLQueryableStructure structure) {
		if (!(structure instanceof TableStructure)) {
			return Collections.emptyList();
		}

		final TableStructure tableStructure = (TableStructure) structure;

		if (tableStructure.getConstraints() == null) {
			return Collections.emptyList();
		}

		return Arrays.stream(tableStructure.getConstraints())
				.filter(ForeignKeyData.class::isInstance)
				.map(ForeignKeyData.class::cast)
				.collect(Collectors.toList());
	}

	private <T extends DatabaseEntry> void scanTableLinks(final AbstractDBTable<T> instance) {
		final Class<? extends SQLQueryable<?>> tableClazz = instance.getTargetClass();

		final TableStructure tableStructure = ((AbstractDBTable<?>) instance).getStructure();
		final Class<? extends DatabaseEntry> entryClazz = tableStructure.getEntryClass();

		final List<ConstraintData> constraints = new LinkedList<>();
		final Set<ColumnData> primaryKeys = new LinkedHashSet<>();
		final Map<Integer, Set<ColumnData>> uniqueGroups = new LinkedHashMap<>();
		final Set<Triplet<ColumnData, String, String>> checks = new HashSet<>();
		final Set<ColumnData> fkCandidates = new HashSet<>();
		final Map<Class<? extends SQLQueryable<?>>, Map<Integer, String>> fkExplicitName = new HashMap<>();
		final Map<ReadOnlyPair<String, Class<? extends SQLQueryable<?>>>, Map<Integer, Set<ColumnData>>> foreignKeys = new LinkedHashMap<>();

		for (final ColumnData columnData : tableStructure.getColumns()) {
			final String columnName = columnData.getLocalName();
			final Field field = columnData.getField();

			// PRIMARY KEY
			if (columnData.isPrimaryKey()) {
				primaryKeys.add(columnData);
			}

			// UNIQUE
			if (columnData.isUnique()) {
				for (final Map<String, Object> unique : columnData.<List<Map<String, Object>>>getHint(DefaultColumnHints.UNIQUE)) {
					final int group = (Integer) unique.get(DefaultColumnHints.UNIQUE_INDEX);
					uniqueGroups.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(columnData);
				}
			}

			// FOREIGN KEY
			if (columnData.isForeignKey()) {
				fkCandidates.add(columnData);
				if (columnData.hasHint(DefaultColumnHints.FOREIGN_KEY_TABLE_NAME)) {
					final Class<? extends SQLQueryable<?>> clazz = columnData.getHint(DefaultColumnHints.FOREIGN_KEY_TABLE);
					final int groupId = columnData.getHint(DefaultColumnHints.FOREIGN_KEY_GROUP_ID, 0);
					fkExplicitName.computeIfAbsent(clazz, k -> new HashMap<>())
							.merge(groupId,
									columnName,
									PCUtils.throwIfNotEqual((a, b) -> new IllegalArgumentException("Opposing table names for foreign key: "
											+ clazz + " with id: " + groupId + "\n" + a + " <> " + b)));
				}
			}

			// CHECK
			if (columnData.hasHint(DefaultColumnHints.CHECK)) {
				for (final Map<String, Object> forEach : columnData.<List<Map<String, Object>>>getHint(DefaultColumnHints.CHECK)) {
					checks.add(Triplets.readOnly(columnData,
							(String) forEach.get(DefaultColumnHints.CHECK_NAME),
							(String) forEach.get(DefaultColumnHints.CHECK_VALUE)));
				}
			}

			String defaultValue = this.computeDefaultValue(instance, columnData);
			if (DefaultValue.I_KNOW.equals(defaultValue) || DefaultValue.NONE.equals(defaultValue)) {
				defaultValue = null;
			} else if (defaultValue == null && !columnData.isNullable() && this.databaseEntryUtils.isForceDefaultValueOnNonNull()
					&& !columnData.isAutoIncrement()) {
				throw new DBException("Column: '" + columnName + "' defined by " + field
						+ " isn't nullable and defines no default value for '" + this.databaseEntryUtils.getDbmsQualifierName() + "'.\n"
						+ "Add @DefaultValue(DefaultValue.I_KNOW) on the field or class to disable this error locally or set the option '"
						+ DatabaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY
						+ "' to false to disable this check globally, you'll need to make sure that this field actually has a value on insertion/update.");
			}
			if (defaultValue == null) {
				columnData.getHints().remove(DefaultColumnHints.DEFAULT_VALUE);
			} else {
				columnData.getHints().put(DefaultColumnHints.DEFAULT_VALUE, defaultValue);
			}
		}

		// do the fks after we go over all columns to create the groups
		for (final ColumnData columnData : fkCandidates) {
			final Class<? extends SQLQueryable<?>> clazz = columnData.getHint(DefaultColumnHints.FOREIGN_KEY_TABLE);
			final int groupId = columnData.getHint(DefaultColumnHints.FOREIGN_KEY_GROUP_ID, 0);
			final String name = fkExplicitName.containsKey(clazz) && fkExplicitName.get(clazz).containsKey(groupId)
					? fkExplicitName.get(clazz).get(groupId)
					: this.getTableName(clazz);
			final ReadOnlyPair<String, Class<? extends SQLQueryable<?>>> key = Pairs.readOnly(name, clazz);
			foreignKeys.computeIfAbsent(key, k -> new LinkedHashMap<>())
					.computeIfAbsent(groupId, k -> new LinkedHashSet<>())
					.add(columnData);
		}

		// CONSTRAINTS
		if (!primaryKeys.isEmpty()) {
			constraints.add(new PrimaryKeyData(tableStructure, primaryKeys.toArray(new ColumnData[0])));
		}

		for (final Set<ColumnData> groupCols : uniqueGroups.values()) {
			constraints.add(new UniqueData(tableStructure, groupCols.toArray(new ColumnData[0])));
		}

		// CHECK ON ENTRY
		if (tableStructure.hasEntryHint(DefaultColumnHints.CHECK)) {
			for (final Map<String, Object> forEach : (List<Map<String, Object>>) tableStructure.getEntryHint(DefaultColumnHints.CHECK)) {
				checks.add(Triplets.readOnly(null,
						(String) forEach.get(DefaultColumnHints.CHECK_NAME),
						(String) forEach.get(DefaultColumnHints.CHECK_VALUE)));
			}
		}

		// CHECK ON TABLE
		if (tableStructure.hasHint(DefaultColumnHints.CHECK)) {
			for (final Map<String, Object> forEach : tableStructure.<List<Map<String, Object>>>getHint(DefaultColumnHints.CHECK)) {
				checks.add(Triplets.readOnly(null,
						(String) forEach.get(DefaultColumnHints.CHECK_NAME),
						(String) forEach.get(DefaultColumnHints.CHECK_VALUE)));
			}
		}

		for (final Triplet<ColumnData, String, String> checkTriplet : checks) {
			final ColumnData column = checkTriplet.getFirst();
			final String name = checkTriplet.getSecond();
			String expr = checkTriplet.getThird();
			if (expr.contains(Check.FIELD_NAME)) {
				throw new DBException("Invalid '" + Check.FIELD_NAME + "' in check '" + name + "': " + expr + " on: "
						+ instance.getTargetClass() + "<" + instance.getEntryClass() + "> (named: " + instance.getName() + ")");
			}
			expr = this.computeExpression(instance, Optional.ofNullable(column), expr);
			if (name != null && !name.trim().isEmpty()) {
				constraints.add(new CheckData(name, expr));
			} else {
				constraints.add(new CheckData(tableStructure, expr));
			}
		}

		final Set<SQLQueryableDependency> dependencies = new HashSet<>();

		for (final Entry<ReadOnlyPair<String, Class<? extends SQLQueryable<?>>>, Map<Integer, Set<ColumnData>>> entry : foreignKeys
				.entrySet()) {
			final ReadOnlyPair<String, Class<? extends SQLQueryable<?>>> key = entry.getKey();
			final Class<? extends SQLQueryable<?>> foreignQueryable = key.getValue();
//				final Class<? extends DatabaseEntry> foreignEntryClazz = this.getEntryType(foreignQueryable);
			final String refTableName = key.getKey();
			final SQLQueryableStructure foreignStructure = this.getStructureFor(foreignQueryable, refTableName);
			final Map<Integer, Set<ColumnData>> grouped = entry.getValue();

			for (final Set<ColumnData> group : grouped.values()) {
				final String[] colNames = group.stream().map(ColumnData::getLocalName).toArray(String[]::new);
				final String[] refCols = group.stream()
						.map(a -> this.getReferencedColumnName(instance, a, foreignStructure))
						.toArray(String[]::new);

				if (PCUtils.duplicates(refCols)) {
					throw new IllegalArgumentException(
							"Foreign key references duplicate columns: " + String.join(", ", refCols) + " to table: " + refTableName);
				}

				constraints.add(new ForeignKeyData(tableStructure, colNames, refTableName, refCols));
			}

			final ColumnData[] pks = this.databaseEntryUtils.getPrimaryKeys(foreignStructure);
			for (final Map.Entry<Integer, Set<ColumnData>> group : grouped.entrySet()) {
				if (pks.length != group.getValue().size()) {
					throw new IllegalArgumentException("Invalid number of foreign keys to table: " + refTableName + ". Expected "
							+ pks.length + " but got: " + group.getValue().size() + " for id: " + group.getKey());
				}
			}

			dependencies.add(new SQLQueryableDependency(foreignQueryable, foreignStructure.getName()));
		}

		tableStructure.setConstraints(constraints.toArray(new ConstraintData[0]));

		// TODO: add manual dependencies
		tableStructure.setDependencies(dependencies);
	}

	private SQLQueryableStructure getStructureFor(final Class<? extends SQLQueryable<?>> foreignQueryable, final String refTableName) {
		if (!this.scanned.containsKey(foreignQueryable)) {
			throw new IllegalArgumentException(
					"No matching DBStructure found for: " + foreignQueryable + " with name: " + refTableName + "\nCandidates: <none>");
		}

		final SQLQueryableStructure[] candidates = this.scanned.get(foreignQueryable)
				.stream()
				.filter(o -> refTableName == null || o.getName().equals(refTableName))
				.toArray(SQLQueryableStructure[]::new);

		if (candidates.length == 1) {
			return candidates[0];
		} else if (candidates.length > 1) {
			throw new IllegalArgumentException(
					"Too many matching DBStructures for: " + foreignQueryable + " with name: " + refTableName + "\nCandidates:\n"
							+ this.scanned.get(foreignQueryable)
									.stream()
									.map(c -> (c.getName().equals(refTableName) ? " Y " : " N ") + c.getName())
									.collect(Collectors.joining("\n")));
		} else {
			throw new IllegalArgumentException(
					"No matching DBStructure found for: " + foreignQueryable + " with name: " + refTableName + "\nCandidates:\n"
							+ this.scanned.get(foreignQueryable)
									.stream()
									.map(c -> (c.getName().equals(refTableName) ? " Y " : " N ") + c.getName())
									.collect(Collectors.joining("\n")));
		}
	}

	public String getReferencedColumnName(
			final SQLQueryable<?> table,
			final ColumnData columnData,
			final SQLQueryableStructure foreignStructure) {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(columnData, "columnData is null.");
		Objects.requireNonNull(foreignStructure, "foreignStructure is null.");

		if (columnData.hasHint(DefaultColumnHints.FOREIGN_KEY_COLUMN)) {
			return this.databaseEntryUtils.resolveSQLQualifiers(table, columnData.getHint(DefaultColumnHints.FOREIGN_KEY_COLUMN));
		}

		final Class<? extends SQLQueryable<?>> refQueryable = foreignStructure.getTargetClass();
		final ColumnData[] refPks = this.databaseEntryUtils.getPrimaryKeys(foreignStructure);

		if (refPks.length > 1) {
			throw new IllegalArgumentException(
					"Foreign key references multiple primary keys in " + refQueryable.getSimpleName() + ". Specify the column explicitly.");
		} else if (refPks.length == 1) {
			return refPks[0].getLocalName();
		} else {
			throw new IllegalArgumentException(
					"Foreign key references no primary key in " + refQueryable.getSimpleName() + ". Specify the column explicitly.");
		}
	}

	private String getTableName(final Class<? extends SQLQueryable<?>> key) {
		final List<SQLQueryableStructure> candidates = this.scanned.get(key);
		if (candidates.size() == 1) {
			return candidates.get(0).getName();
		} else if (candidates.size() == 0) {
			throw new IllegalArgumentException("No candidate SQLQueryable found for class: " + key);
		} else {
			throw new IllegalArgumentException("Too many candidate SQLQueryable found for class: " + key + ", precise the name manually.");
		}
	}

	protected TableStructure scanSelfTableStructure(
			final AbstractDBTable<?> instance,
			final Map<String, Object> customHints,
			final Class<? extends AbstractDBTable<?>> tableClazz,
			final Map<String, Object> customEntryHints) {
		final Class<? extends DatabaseEntry> entryClazz = this.getEntryType(tableClazz);
		final Map<String, Object> queryableHints = this.hintScanner.computeQueryableHints(tableClazz);
		if (customHints != null) {
			queryableHints.putAll(customHints);
		}
		final String[] queryableParts = this.structureVisitor.getQueryableNameParts(tableClazz, queryableHints);
		final String queryableName = this.structureVisitor.getQueryableName(tableClazz, queryableHints);
		final Map<String, Object> entryHints = this.hintScanner.computeQueryableHints(entryClazz);
		if (customEntryHints != null) {
			entryHints.putAll(customEntryHints);
		}
		final String qualifiedName = this.structureVisitor.qualifiedName(tableClazz, queryableHints);

		final TableStructure tableStructure = new TableStructure(new StructureName(queryableName, queryableParts, qualifiedName),
				tableClazz,
				entryClazz,
				Collections.unmodifiableMap(queryableHints),
				Collections.unmodifiableMap(entryHints));

		final ColumnData[] columnDatas = this.computeColumnsFor(instance, tableStructure, entryClazz);
		tableStructure.setColumns(columnDatas);

		this.registerSimpleNames(tableClazz, queryableHints, tableStructure);

		return tableStructure;
	}

	protected void registerSimpleNames(
			final Class<? extends SQLQueryable<?>> tableClazz,
			final Map<String, Object> queryableHints,
			final SQLQueryableStructure tableStructure) {
		if (queryableHints.containsKey(DefaultQueryableHints.DEFINED_NAME)) {
			this.database.getStructure()
					.getSimpleNames()
					.put((String) queryableHints.get(DefaultQueryableHints.DEFINED_NAME), tableStructure);
		}
		this.database.getStructure().getSimpleNames().put(tableClazz.getSimpleName(), tableStructure);
		this.database.getStructure()
				.getLinkedNames()
				.computeIfAbsent(tableClazz.getSimpleName(), k -> new HashMap<>())
				.put(tableStructure.getName(), tableStructure);
	}

	protected ViewStructure scanSelfViewStructure(
			final AbstractDBView<? extends DatabaseEntry> instance,
			final Map<String, Object> customHints,
			final Class<? extends AbstractDBView<? extends DatabaseEntry>> viewClazz,
			final Map<String, Object> customEntryHints) {
		final Class<? extends DatabaseEntry> entryClazz = this.getEntryType(viewClazz);
		final Map<String, Object> queryableHints = this.hintScanner.computeQueryableHints(viewClazz);
		if (customHints != null) {
			queryableHints.putAll(customHints);
		}
		final String[] queryableParts = this.structureVisitor.getQueryableNameParts(viewClazz, queryableHints);
		final String queryableName = this.structureVisitor.getQueryableName(viewClazz, queryableHints);
		final Map<String, Object> entryHints = this.hintScanner.computeQueryableHints(entryClazz);
		if (customEntryHints != null) {
			entryHints.putAll(customEntryHints);
		}
		final String qualifiedName = this.structureVisitor.qualifiedName(viewClazz, queryableHints);

		final StructureName structureName = new StructureName(queryableName, queryableParts, qualifiedName);
		final ViewStructure viewStructure = new ViewStructure(structureName, viewClazz, entryClazz, queryableHints, entryHints);

		final ColumnData[] columns = this.computeColumnsFor(instance, viewStructure, entryClazz);
		viewStructure.setColumns(columns);

		this.registerSimpleNames(viewClazz, queryableHints, viewStructure);

		return viewStructure;
	}

	protected void scanViewLinks(final AbstractDBView<? extends DatabaseEntry> instance) {
		final ViewStructure viewStructure = instance.getStructure();

		final Map<String, Object> queryableHints = viewStructure.getHints();

		final List<ViewCommonTableExpressionStructure> withTables = new ArrayList<>();
		final List<ViewTableStructure> tables = new ArrayList<>();
		final List<UnionTableStructure> unionTables = new ArrayList<>();
		final List<String> groupBys = new ArrayList<>();
		final List<ViewOrderStructure> orderBys = new ArrayList<>();

		if (queryableHints.containsKey(DefaultQueryableHints.VIEW_GROUP_BY)) {
			Collections.addAll(groupBys, (String[]) queryableHints.get(DefaultQueryableHints.VIEW_GROUP_BY));
		}

		if (queryableHints.containsKey(DefaultQueryableHints.VIEW_ORDER_BY)) {
			for (final Map<String, Object> orderBy : (List<Map<String, Object>>) queryableHints.get(DefaultQueryableHints.VIEW_ORDER_BY)) {
				orderBys.add(this.buildOrderBy(orderBy));
			}
		}

		if (queryableHints.containsKey(DefaultQueryableHints.VIEW_ORDER_BY)) {
			for (final Map<String, Object> viewWithTable : (List<Map<String, Object>>) queryableHints
					.get(DefaultQueryableHints.VIEW_ORDER_BY)) {
				withTables.add(this.buildWith(viewWithTable));
			}
		}

		if (queryableHints.containsKey(DefaultQueryableHints.VIEW_TABLES)) {
			for (final Map<String, Object> table : (List<Map<String, Object>>) queryableHints.get(DefaultQueryableHints.VIEW_TABLES)) {
				tables.add(this.buildTable(table));
			}
		}

		final String customSQL = (String) queryableHints.get(DefaultQueryableHints.VIEW_CUSTOM_SQL);
		final String condition = (String) queryableHints.get(DefaultQueryableHints.VIEW_CONDITION);

		viewStructure.setWithTables(withTables.toArray(new ViewCommonTableExpressionStructure[0]));
		viewStructure.setTables(tables.toArray(new ViewTableStructure[0]));
		viewStructure.setUnionTables(unionTables.toArray(new UnionTableStructure[0]));
		viewStructure.setGroupBy(groupBys.toArray(new String[0]));
		viewStructure.setOrderBy(orderBys.toArray(new ViewOrderStructure[0]));
		final boolean distinct = viewStructure.getMainTable().isDistinct();
		viewStructure.setDistinct(distinct);
		viewStructure.setCustomSQL(customSQL);
		viewStructure.setCondition(condition);
		// TODO: add manual dependencies for custom sql

		final Set<SQLQueryableDependency> dependencies = new HashSet<>();
		withTables.forEach(c -> dependencies.addAll(c.getDependencies()));
		tables.forEach(c -> dependencies.addAll(c.getDependencies()));
		unionTables.forEach(c -> dependencies.addAll(c.getDependencies()));
		viewStructure.setDependencies(dependencies);
	}

	private ViewCommonTableExpressionStructure buildWith(final Map<String, Object> viewWithTable) {
		final ViewCommonTableExpressionStructure ws = new ViewCommonTableExpressionStructure(
				(String) viewWithTable.get(DefaultQueryableHints.VIEW_AS_NAME),
				(String) viewWithTable.get(DefaultQueryableHints.VIEW_CONDITION));

		final List<ViewColumnStructure> columns = new ArrayList<>();
		final List<ViewTableStructure> tables = new ArrayList<>();
		final List<String> groupBys = new ArrayList<>();
		final List<ViewOrderStructure> orderBys = new ArrayList<>();

		for (final Map<String, Object> columnMap : (List<Map<String, Object>>) viewWithTable.get(DefaultQueryableHints.VIEW_COLUMNS)) {
			columns.add(this.buildColumn(columnMap));
		}

		for (final Map<String, Object> tableMap : (List<Map<String, Object>>) viewWithTable.get(DefaultQueryableHints.VIEW_TABLES)) {
			tables.add(this.buildTable(tableMap));
		}

		Collections.addAll(groupBys, (String[]) viewWithTable.get(DefaultQueryableHints.VIEW_GROUP_BY));

		for (final Map<String, Object> orderBy : (List<Map<String, Object>>) viewWithTable.get(DefaultQueryableHints.VIEW_ORDER_BY)) {
			orderBys.add(this.buildOrderBy(orderBy));
		}

		ws.setColumns(columns.toArray(new ViewColumnStructure[0]));
		ws.setTables(tables.toArray(new ViewTableStructure[0]));
		ws.setGroupBy(groupBys.toArray(new String[0]));
		ws.setOrderBy(orderBys.toArray(new ViewOrderStructure[0]));

		final Set<SQLQueryableDependency> dependencies = new HashSet<>();
		Arrays.stream(ws.getTables()).forEach(table -> dependencies.add(table.getKey()));
		ws.setDependencies(dependencies);

		return ws;
	}

	private ViewOrderStructure buildOrderBy(final Map<String, Object> orderBy) {
		final ViewOrderStructure vos = new ViewOrderStructure((String) orderBy.get(DefaultQueryableHints.VIEW_ORDER_BY_COLUMN),
				(OrderBy.Type) orderBy.getOrDefault(DefaultQueryableHints.VIEW_ORDER_BY_DIR, OrderBy.Type.ASC));
		Objects.requireNonNull(vos.getColumn(), "ORDER BY column cannot be blank/null.");
		return vos;
	}

	private ViewColumnStructure buildColumn(final Map<String, Object> columnMap) {
		final ViewColumnStructure cs = new ViewColumnStructure((String) columnMap.get(DefaultQueryableHints.VIEW_COLUMN_NAME),
				(String) columnMap.get(DefaultQueryableHints.VIEW_COLUMN_AS_NAME),
				(String) columnMap.get(DefaultQueryableHints.VIEW_COLUMN_FUNCTION));
		if (cs.getFunc() == null && cs.getName() == null) {
			throw new NullPointerException("Column isn't defined by name nor function.");
		}
		if (cs.getFunc() != null && cs.getAlias() == null) {
			throw new NullPointerException("Column with function requires an alias.");
		}
		return cs;
	}

	private ViewTableStructure buildTable(final Map<String, Object> tableMap) {
		final String foreignName = (String) tableMap.get(DefaultQueryableHints.VIEW_NAME);
		final Class<? extends SQLQueryable<?>> foreignClass = (Class<? extends SQLQueryable<?>>) tableMap
				.get(DefaultQueryableHints.VIEW_TYPE);

		final ViewTableStructure ts = new ViewTableStructure(foreignName,
				foreignClass,
				this.getStructureFor(foreignClass, foreignName).getStructureName(),
				(String) tableMap.get(DefaultQueryableHints.VIEW_AS_NAME),
				(String) tableMap.get(DefaultQueryableHints.VIEW_JOIN_ON_CONDITION),
				(ViewTable.Type) tableMap.getOrDefault(DefaultQueryableHints.VIEW_JOIN_TYPE, ViewTable.Type.MAIN),
				(boolean) tableMap.getOrDefault(DefaultQueryableHints.VIEW_DISTINCT, false));

		Objects.requireNonNull(ts.getAlias(), "Alias cannot be blank/null.");

		for (final Map<String, Object> columnMap : (List<Map<String, Object>>) tableMap.get(DefaultQueryableHints.VIEW_COLUMNS)) {
			ts.getColumns().add(this.buildColumn(columnMap));
		}

		return ts;
	}

	protected ColumnData[] computeColumnsFor(
			final SQLQueryable<?> table,
			final SQLQueryableStructure tableStructure,
			final Class<? extends DatabaseEntry> entryClazz) {
		final List<ColumnData> columns = new ArrayList<>();

		for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
			field.setAccessible(true);

			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			final String columnName = this.databaseEntryUtils.fieldToColumnName(field);
			final Map<String, Object> typeHints = this.hintScanner.computeTypeHints(field.getAnnotatedType());
			final ColumnType columnType = this.databaseEntryUtils.getColumnTypeProvider().getTypeFor(field.getAnnotatedType(), typeHints);
			final Map<String, Object> columnHints = this.hintScanner.computeColumnHints(field);

			final boolean nullable = (boolean) columnHints.getOrDefault(DefaultColumnHints.NULLABLE, false);

			if (nullable && field.getType().isPrimitive()) {
				throw new DBException("Column: '" + columnName + "' defined by " + field + " is a nullable of primitive type.");
			}

			final String[] fullColumnNameParts = new String[tableStructure.getNameParts().length + 1];
			System.arraycopy(tableStructure.getNameParts(), 0, fullColumnNameParts, 0, tableStructure.getNameParts().length);
			fullColumnNameParts[tableStructure.getNameParts().length] = columnName;
			final ColumnData columnData = new ColumnData(columnName,
					this.structureVisitor.qualifiedName(columnName),
					new StructureName(Arrays.stream(fullColumnNameParts).collect(Collectors.joining(".")),
							fullColumnNameParts,
							this.structureVisitor.qualifiedName(fullColumnNameParts)),
					typeHints,
					columnType,
					field,
					columnHints);

			columns.add(columnData);
		}

		return columns.toArray(new ColumnData[0]);
	}

	public String computeDefaultValue(final SQLQueryable<?> table, final ColumnData columnData) {
		if (!columnData.hasHint(DefaultColumnHints.DEFAULT_VALUE)) {
			return null;
		}

		final String input = columnData.<String>getHint(DefaultColumnHints.DEFAULT_VALUE);

		if (DefaultValue.NONE.equals(input) || DefaultValue.I_KNOW.equals(input)) {
			return input;
		} else if (DefaultValue.NULL.equals(input)) {
			return null;
		}

		return this.computeExpression(table, Optional.of(columnData), input);
	}

	public String computeExpression(final SQLQueryable<?> table, final Optional<ColumnData> columnData, final String input) {
		if (input == null) {
			return null;
		}

		final Map<String, String> map = new HashMap<>();
		map.put(DatabaseEntryUtils.TABLE_NAME_KEY, table.getQualifiedName());
		columnData.ifPresent(cd -> map.put(DatabaseEntryUtils.FIELD_NAME_KEY, columnData.get().getQualifiedName()));
		return this.databaseEntryUtils.resolveSQLQualifiers(table, input, map);
	}

	public <T extends DatabaseEntry> Field getFieldFor(final Class<T> entryClazz, final String sqlName) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(sqlName, "sqlName is null.");

		try {
			final Field field = this.findField(entryClazz, sqlName);
			if (field != null && field.isAnnotationPresent(Column.class)
					&& this.databaseEntryUtils.fieldToColumnName(field).equals(sqlName)) {
				return field;
			}
		} catch (final NoSuchFieldException e) {
			// ignore
		}

		for (final Field field : PCUtils.getAllFields(entryClazz)) {
			if (field.isAnnotationPresent(Column.class) && this.databaseEntryUtils.fieldToColumnName(field).equals(sqlName)) {
				return field;
			}
		}

		throw new IllegalArgumentException("No field for column named: '" + sqlName + "' in class: [" + entryClazz.getName() + "]");
	}

	protected Field findField(final Class<?> type, final String name) throws NoSuchFieldException {
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			try {
				return c.getDeclaredField(name);
			} catch (final NoSuchFieldException e) {
				// keep going
			}
		}
		throw new NoSuchFieldException(name);
	}

	protected Field[] getAllFields(final Class<?> type) {
		final List<Field> fields = new ArrayList<>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields.toArray(new Field[fields.size()]);
	}

	public List<Field> sortFields(final Field[] fields) {
		Objects.requireNonNull(fields, "fields is null.");

		final List<Field> pkFields = new ArrayList<>();
		final List<Field> fkFields = new ArrayList<>();
		final List<Field> otherFields = new ArrayList<>();

		for (final Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				pkFields.add(field);
			} else if (field.isAnnotationPresent(ForeignKey.class)) {
				fkFields.add(field);
			} else {
				otherFields.add(field);
			}
		}

		final Comparator<Field> byName = Comparator.comparing(Field::getName);

		pkFields.sort(byName);
		otherFields.sort(byName);
		fkFields.sort(byName);

		final List<Field> sorted = new ArrayList<>(pkFields);
		sorted.addAll(otherFields);
		sorted.addAll(fkFields);

		return sorted;
	}

	public <T extends DatabaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> tableClass) {
		Objects.requireNonNull(tableClass, "tableClass is null.");

		final Class<?> result = this.findEntryType(tableClass);

		if (result != null) {
			return (Class<T>) result;
		}

		throw new IllegalArgumentException("Could not determine DatabaseEntry type from " + tableClass.getName());
	}

	private Class<?> findEntryType(final Class<?> type) {
		if (type == null || type == Object.class) {
			return null;
		}

		for (final Type genericInterface : type.getGenericInterfaces()) {
			final Class<?> result = this.findEntryType(genericInterface);

			if (result != null) {
				return result;
			}
		}

		final Class<?> resultFromSuperclass = this.findEntryType(type.getGenericSuperclass());

		if (resultFromSuperclass != null) {
			return resultFromSuperclass;
		}

		for (final Class<?> iface : type.getInterfaces()) {
			final Class<?> result = this.findEntryType(iface);

			if (result != null) {
				return result;
			}
		}

		return this.findEntryType(type.getSuperclass());
	}

	private Class<?> findEntryType(final Type type) {
		if (type == null) {
			return null;
		}

		if (type instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) type;
			final Class<?> rawType = PCUtils.getRawClass(pt.getRawType());

			if (rawType != null && SQLQueryable.class.isAssignableFrom(rawType)) {
				for (final Type typeArg : pt.getActualTypeArguments()) {
					final Class<?> rawArg = PCUtils.getRawClass(typeArg);

					if (rawArg != null && DatabaseEntry.class.isAssignableFrom(rawArg)) {
						return rawArg;
					}
				}
			}

			return this.findEntryType(rawType);
		}

		if (type instanceof Class<?>) {
			return this.findEntryType((Class) type);
		}

		return null;
	}

}
