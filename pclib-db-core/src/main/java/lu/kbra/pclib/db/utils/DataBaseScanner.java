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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
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
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DBStructure;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtilsOptionsOwner;
import lu.kbra.pclib.db.view.AbstractDBView;

@Getter
@ToString
@EqualsAndHashCode
public class DataBaseScanner {

	protected final DataBase dataBase;
	protected final DataBaseEntryUtils dataBaseEntryUtils;
	protected final SQLStructureVisitor structureVisitor;
	protected final List<ReadOnlyTriplet<SQLQueryable<?>, Optional<Map<String, Object>>, Optional<Map<String, Object>>>> forScan = new ArrayList<>();
	protected final SQLFunctionResolver functionResolver;
	protected final Map<String, TableStructure> simpleNameCache = new HashMap<>();
	protected final Map<Class<? extends SQLQueryable<?>>, List<DBStructure>> scanned = new HashMap<>();
	protected final Map<String, Object> baseHints;
	protected final HintScanner hintScanner;

	public DataBaseScanner(final DataBase dataBase, Map<String, Object> hints) {
		this.dataBase = dataBase;
		this.baseHints = hints == null ? new HashMap<>() : hints;
		if (dataBase.getCustomHints() != null) {
			this.baseHints.putAll(dataBase.getCustomHints());
		}
		this.dataBaseEntryUtils = dataBase.getDataBaseEntryUtils();
		this.structureVisitor = this.dataBaseEntryUtils.getStructureVisitor();
		this.hintScanner = dataBaseEntryUtils.getHintScanner();
		this.functionResolver = dataBaseEntryUtils.getFunctionResolver();
	}

	public <B extends SQLQueryable<T>, T extends DataBaseEntry> DataBaseScanner register(final B instance) {
		this.forScan.add(Triplets.readOnly(instance, Optional.empty(), Optional.empty()));
		return this;
	}

	public <B extends SQLQueryable<T>, T extends DataBaseEntry> DataBaseScanner
			register(final B instance, final Map<String, Object> queryableHints, final Map<String, Object> entryHints) {
		this.forScan.add(Triplets.readOnly(instance, Optional.ofNullable(queryableHints), Optional.ofNullable(entryHints)));
		return this;
	}

	public <T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final String sqlName) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(sqlName, "sqlName is null.");

		try {
			final Field field = this.findField(entryClazz, sqlName);
			if (field != null && field.isAnnotationPresent(Column.class) && dataBaseEntryUtils.fieldToColumnName(field).equals(sqlName)) {
				return field;
			}
		} catch (final NoSuchFieldException e) {
			// ignore
		}

		for (final Field field : PCUtils.getAllFields(entryClazz)) {
			if (field.isAnnotationPresent(Column.class) && dataBaseEntryUtils.fieldToColumnName(field).equals(sqlName)) {
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

	public void doScan() {
		this.scanSelfStructure();
		this.scanLinks();

		final DataBaseStructure structure;
		if (dataBase.getDataBaseStructure() == null) {
			final String name = (String) baseHints.computeIfAbsent(DefaultQueryableHints.NAME_OVERRIDE, k -> {
				throw new IllegalStateException("DataBase has no name.");
			});
			final String queryableName = structureVisitor.qualifiedName(name);
			structure = new DataBaseStructure(name, queryableName, baseHints);
			this.dataBase.setDataBaseStructure(structure);
		} else {
			structure = dataBase.getDataBaseStructure();
		}

		scanned.values().forEach(t -> t.forEach(q -> {
			if (q instanceof TableStructure) {
				structure.getTableStructures().add((TableStructure) q);
			} else if (q instanceof ViewStructure) {
				structure.getViewStructures().add((ViewStructure) q);
			} else {
				throw new IllegalArgumentException("Unknown DBStructure type: " + q + " (" + t.getClass() + ")");
			}
		}));

		forScan.clear();
		scanned.clear();
	}

	@SuppressWarnings("unchecked")
	protected void scanLinks() {
		for (final ReadOnlyTriplet<SQLQueryable<?>, Optional<Map<String, Object>>, Optional<Map<String, Object>>> triplet : this.forScan) {
			final SQLQueryable<?> instance = triplet.getFirst();
//			final Map<String, Object> queryableHints = triplet.getSecond().orElse(null);
//			final Map<String, Object> entryHints = triplet.getThird().orElse(null);

			final Class<? extends SQLQueryable<?>> tableClazz = instance.getTargetClass();
			if (!AbstractDBTable.class.isAssignableFrom(tableClazz)) {
				continue;
			}

			final TableStructure tableStructure = ((AbstractDBTable<?>) instance).getStructure();
			final Class<? extends DataBaseEntry> entryClazz = tableStructure.getEntryClass();

			final List<ConstraintData> constraints = new LinkedList<>();
			final Set<ColumnData> primaryKeys = new LinkedHashSet<>();
			final Map<Integer, Set<ColumnData>> uniqueGroups = new LinkedHashMap<>();
			final Set<Triplet<ColumnData, String, String>> checks = new HashSet<>();
			final Set<ColumnData> fkCandidates = new HashSet<>();
			final Map<Class<? extends SQLQueryable<?>>, Map<Integer, String>> fkExplicitName = new HashMap<>();
			final Map<ReadOnlyPair<String, Class<? extends SQLQueryable<?>>>, Map<Integer, Set<ColumnData>>> foreignKeys = new LinkedHashMap<>();

			for (final ColumnData columnData : tableStructure.getColumns()) {
				final String columnName = columnData.getLocalName();

				// PRIMARY KEY
				if (columnData.isPrimaryKey()) {
					primaryKeys.add(columnData);
				}

//				System.out.println("tree " + columnData.getName());
//				PCUtils.printTree(columnData.getHints(), System.out);
//
//				System.out.println("type " + columnData.getName());
//				PCUtils.printTree(columnData.getTypeHints(), System.out);

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
										PCUtils.throwIfNotEqual(
												(a, b) -> new IllegalArgumentException("Opposing table names for foreign key: " + clazz
														+ " with id: " + groupId + "\n" + a + " <> " + b)));
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
				for (final Map<String, Object> forEach : (List<Map<String, Object>>) tableStructure
						.getEntryHint(DefaultColumnHints.CHECK)) {
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

			final Map<String, String> map = new HashMap<>();
			map.put(DataBaseEntryUtils.TABLE_NAME_KEY, this.structureVisitor.qualifiedName(tableStructure.getName()));
			for (final Triplet<ColumnData, String, String> checkTriplet : checks) {
				final ColumnData column = checkTriplet.getFirst();
				final String name = checkTriplet.getSecond();
				String expr = checkTriplet.getThird();
				if (expr.contains(Check.FIELD_NAME)) {
					throw new DBException(
							"Invalid '" + Check.FIELD_NAME + "' in check '" + name + "': " + expr + " on class: " + entryClazz);
				}
				if (column != null) {
					map.put(DataBaseEntryUtils.FIELD_NAME_KEY, column.getLocalQualifiedName());
				} else {
					map.remove(DataBaseEntryUtils.FIELD_NAME_KEY);
				}
				expr = this.replaceSQLQualifiers(tableClazz, expr, map);
				if (name != null && !name.trim().isEmpty()) {
					constraints.add(new CheckData(name, expr));
				} else {
					constraints.add(new CheckData(tableStructure, expr));
				}
			}

			for (final Entry<ReadOnlyPair<String, Class<? extends SQLQueryable<?>>>, Map<Integer, Set<ColumnData>>> entry : foreignKeys
					.entrySet()) {
				final ReadOnlyPair<String, Class<? extends SQLQueryable<?>>> key = entry.getKey();
				final Class<? extends SQLQueryable<?>> foreignQueryable = key.getValue();
//				final Class<? extends DataBaseEntry> foreignEntryClazz = this.getEntryType(foreignQueryable);
				final String refTableName = key.getKey();
				final DBStructure foreignStructure = this.scanned.get(foreignQueryable)
						.stream()
						.filter(o -> o.getName().equals(refTableName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException(
								"No matching DBStructure found for: " + foreignQueryable + " with name: " + refTableName));
				final Map<Integer, Set<ColumnData>> grouped = entry.getValue();

				for (final Set<ColumnData> group : grouped.values()) {
					final String[] colNames = group.stream().map(ColumnData::getName).toArray(String[]::new);
					final String[] refCols = group.stream()
							.map(a -> this.getReferencedColumnName(tableStructure, a, foreignStructure))
							.toArray(String[]::new);

					if (PCUtils.duplicates(refCols)) {
						throw new IllegalArgumentException(
								"Foreign key references duplicate columns: " + String.join(", ", refCols) + " to table: " + refTableName);
					}

					constraints.add(new ForeignKeyData(tableStructure, colNames, refTableName, refCols));
				}

				final ColumnData[] pks = dataBaseEntryUtils.getPrimaryKeys(foreignStructure);
				for (final Map.Entry<Integer, Set<ColumnData>> group : grouped.entrySet()) {
					if (pks.length != group.getValue().size()) {
						throw new IllegalArgumentException("Invalid number of foreign keys to table: " + refTableName + ". Expected "
								+ pks.length + " but got: " + group.getValue().size() + " for id: " + group.getKey());
					}
				}
			}

			tableStructure.setConstraints(constraints.toArray(new ConstraintData[0]));
		}
	}

	public String
			getReferencedColumnName(final DBStructure thisStructure, final ColumnData columnData, final DBStructure foreignStructure) {
		Objects.requireNonNull(columnData, "columnData is null.");

		if (!columnData.hasHint(DefaultColumnHints.FOREIGN_KEY_COLUMN)) {
			return this.replaceSQLQualifiers(thisStructure.getTargetClass(),
					columnData.getHint(DefaultColumnHints.FOREIGN_KEY_COLUMN),
					Collections.emptyMap());
		}

		final Class<? extends SQLQueryable<?>> refQueryable = foreignStructure.getTargetClass();
		final ColumnData[] refPks = dataBaseEntryUtils.getPrimaryKeys(foreignStructure);

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
		final List<DBStructure> candidates = this.scanned.get(key);
		if (candidates.size() == 1) {
			return candidates.get(0).getName();
		} else if (candidates.size() == 0) {
			throw new IllegalArgumentException("No candidate SQLQueryable found for class: " + key);
		} else {
			throw new IllegalArgumentException("Too many candidate SQLQueryable found for class: " + key + ", precise the name manually.");
		}
	}

	public String
			replaceSQLQualifiers(final Class<? extends SQLQueryable<?>> tableClazz, final String input, final Map<String, String> data) {
		Objects.requireNonNull(tableClazz, "tableClazz is null.");
		Objects.requireNonNull(input, "input is null.");

		final Pattern pattern = Pattern.compile("\\{([^}]+)}");

		final Matcher matcher = pattern.matcher(input);
		final StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			final String token = matcher.group(1);

			final String replacement;

			if (data.containsKey(token)) {
				replacement = data.get(token);
			} else if (token.startsWith(DataBaseEntryUtils.QUALIFIER_KEY)) {
				final String value = token.substring(Query.QUALIFIER_KEY.length());
				replacement = this.structureVisitor.qualifiedName(value);
			} else if (token.startsWith(DataBaseEntryUtils.FUNCTION_KEY)) {
				final String value = token.substring(Query.FUNCTION_KEY.length());
				replacement = this.functionResolver.apply(value);
			} else if (token.startsWith(DataBaseEntryUtils.MEMBER_KEY)) {
				final String value = token.substring(Query.MEMBER_KEY.length());
				final Class<? extends DataBaseEntry> entryClazz = this.getEntryType(tableClazz);
				try {
					final Field field = this.findField(entryClazz, value);
					replacement = this.structureVisitor.qualifiedName(dataBaseEntryUtils.fieldToColumnName(field));
				} catch (final NoSuchFieldException e) {
					throw new DBException("No column field found matching: " + value + " on: " + entryClazz, e);
				}
			} else {
				replacement = matcher.group(0);
			}

			matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}

		matcher.appendTail(result);

		return result.toString();
	}

	protected void scanSelfStructure() {
		for (final ReadOnlyTriplet<SQLQueryable<?>, Optional<Map<String, Object>>, Optional<Map<String, Object>>> pair : this.forScan) {
			final SQLQueryable<?> instance = pair.getFirst();
			final Map<String, Object> queryableHints = pair.getSecond().orElse(null);
			final Map<String, Object> entryHints = pair.getThird().orElse(null);
			final Class<? extends SQLQueryable<?>> tableClazz = (Class<? extends SQLQueryable<?>>) pair.getSecond()
					.map(c -> c.get(DefaultQueryableHints.TARGET_CLASS))
					.orElse(instance.getClass());

			if (instance instanceof AbstractDBTable<?>) {
				final TableStructure tableStructure = this.scanSelfTableStructure((AbstractDBTable<?>) instance,
						queryableHints,
						tableClazz.asSubclass(AbstractDBTable.class),
						entryHints);
				((AbstractDBTable<?>) instance).setTableStructure(tableStructure);
				scanned.computeIfAbsent(tableClazz, k -> new ArrayList<>(1)).add(tableStructure);
			} else if (instance instanceof AbstractDBView<?>) {
				final ViewStructure viewStructure = this.scanSelfViewStructure((AbstractDBView<?>) instance,
						queryableHints,
						tableClazz.asSubclass(AbstractDBView.class),
						entryHints);
				((AbstractDBView<?>) instance).setViewStructure(viewStructure);
				scanned.computeIfAbsent(tableClazz, k -> new ArrayList<>(1)).add(viewStructure);
			}
		}
	}

	protected <T extends DataBaseEntry> TableStructure scanSelfTableStructure(
			final AbstractDBTable<T> instance,
			final Map<String, Object> customHints,
			final Class<? extends AbstractDBTable<T>> tableClazz,
			final Map<String, Object> customEntryHints) {
		final Class<T> entryClazz = this.getEntryType(tableClazz);
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

		if (queryableHints.containsKey(DefaultQueryableHints.DEFINED_NAME)) {
			this.simpleNameCache.put((String) queryableHints.get(DefaultQueryableHints.DEFINED_NAME), tableStructure);
		}
		this.simpleNameCache.put(tableClazz.getSimpleName(), tableStructure);

		return tableStructure;
	}

	protected <T extends DataBaseEntry> ViewStructure scanSelfViewStructure(
			final AbstractDBView<T> instance,
			final Map<String, Object> customHints,
			final Class<? extends AbstractDBView<T>> tableClazz,
			final Map<String, Object> customEntryHints) {
		final Class<T> entryClazz = this.getEntryType(tableClazz);
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

		PCUtils.printTree(hintScanner.computeQueryableHints(tableClazz), System.out);

//		final ViewStructure tableStructure = new ViewStructure(new StructureName(queryableName, queryableParts, qualifiedName),
//				tableClazz,
//				entryClazz,
//				Collections.unmodifiableMap(queryableHints),
//				Collections.unmodifiableMap(entryHints));
//
//		final ColumnData[] columnDatas = this.computeColumnsFor(instance, tableStructure, entryClazz);
//		tableStructure.setColumns(columnDatas);
//
//		if (queryableHints.containsKey(DefaultQueryableHints.DEFINED_NAME)) {
//			this.simpleNameCache.put((String) queryableHints.get(DefaultQueryableHints.DEFINED_NAME), tableStructure);
//		}
//		this.simpleNameCache.put(tableClazz.getSimpleName(), tableStructure);
//
//		return tableStructure;
		return null;
	}

	protected <T extends DataBaseEntry> ColumnData[]
			computeColumnsFor(SQLQueryable<T> table, TableStructure tableStructure, final Class<T> entryClazz) {
		final List<ColumnData> columns = new ArrayList<>();

		for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
			field.setAccessible(true);

			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			final String columnName = dataBaseEntryUtils.fieldToColumnName(field);
			final Map<String, Object> typeHints = this.hintScanner.computeTypeHints(field.getAnnotatedType());
			final ColumnType columnType = this.dataBaseEntryUtils.getColumnTypeProvider().getTypeFor(field.getAnnotatedType(), typeHints);
			final Map<String, Object> columnHints = this.hintScanner.computeColumnHints(field);

			final boolean autoIncrement = (boolean) columnHints.getOrDefault(DefaultColumnHints.AUTO_INCREMENT, false);
			final boolean nullable = (boolean) columnHints.getOrDefault(DefaultColumnHints.NULLABLE, false);

			if (nullable && field.getType().isPrimitive()) {
				throw new DBException("Column: '" + columnName + "' defined by " + field + " is a nullable of primitive type.");
			}

			String defaultValue = (String) columnHints.get(DefaultColumnHints.DEFAULT_VALUE);
			if (DefaultValue.I_KNOW.equals(defaultValue) || DefaultValue.NONE.equals(defaultValue)) {
				defaultValue = null;
			} else if (defaultValue == null && !nullable && this.dataBaseEntryUtils.isForceDefaultValueOnNonNull() && !autoIncrement) {
				throw new DBException("Column: '" + columnName + "' defined by " + field
						+ " isn't nullable and defines no default value for '" + this.dataBaseEntryUtils.getDbmsQualifierName() + "'.\n"
						+ "Add @DefaultValue(DefaultValue.I_KNOW) on the field or class to disable this error locally or set the option '"
						+ DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY
						+ "' to false to disable this check globally, you'll need to make sure that this field actually has a value on insertion/update.");
			}

//			final String onUpdate = (String) columnHints.get(DefaultColumnHints.ON_UPDATE);
//			final boolean primaryKey = (boolean) columnHints.getOrDefault(DefaultColumnHints.PRIMARY_KEY, false);
//			final boolean unique = columnHints.containsKey(DefaultColumnHints.UNIQUE_INDEX);
//			final boolean foreignKey = columnHints.containsKey(DefaultColumnHints.FOREIGN_KEY_TABLE);

			final String[] fullColumnNameParts = new String[tableStructure.getNameParts().length + 1];
			System.arraycopy(tableStructure.getNameParts(), 0, fullColumnNameParts, 0, tableStructure.getNameParts().length);
			fullColumnNameParts[tableStructure.getNameParts().length] = columnName;
			final ColumnData columnData = new ColumnData(columnName,
					structureVisitor.qualifiedName(columnName),
					new StructureName(Arrays.stream(fullColumnNameParts).collect(Collectors.joining(".")),
							fullColumnNameParts,
							structureVisitor.qualifiedName(fullColumnNameParts)),
					typeHints,
					columnType,
					field,
					columnHints);

			columns.add(columnData);
		}

		return columns.toArray(new ColumnData[0]);
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

	public <T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> tableClass) {
		Objects.requireNonNull(tableClass, "tableClass is null.");

		final Class<?> result = this.findEntryType(tableClass);

		if (result != null) {
			return (Class<T>) result;
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + tableClass.getName());
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

					if (rawArg != null && DataBaseEntry.class.isAssignableFrom(rawArg)) {
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
