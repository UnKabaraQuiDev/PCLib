package lu.kbra.pclib.db.query;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.query.QueryParameterPart;
import lu.kbra.pclib.db.domain.query.QueryStructure;
import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.view.ViewOrderStructure;
import lu.kbra.pclib.db.domain.view.ViewTableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InvalidPlaceholderException;
import lu.kbra.pclib.db.exception.NoMatchingStructureException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.utils.impl.ColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.QueryFunctionProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DefaultQueryFunctionProvider implements QueryFunctionProvider {

	@Data
	public class HintsStructure implements AbstractDBStructure {

		private final Map<String, Object> hints;

		@Override
		public Map<String, Object> toMap() {
			return this.hints;
		}

	}

	protected DatabaseEntryUtils databaseEntryUtils;
	protected SQLStructureVisitor structureVisitor;
	protected ColumnTypeProvider columnTypeProvider;

	public DefaultQueryFunctionProvider(final DatabaseEntryUtils databaseEntryUtils) {
		this.databaseEntryUtils = databaseEntryUtils;
		this.structureVisitor = databaseEntryUtils.getStructureVisitor();
		this.columnTypeProvider = databaseEntryUtils.getColumnTypeProvider();
	}

	public Query.Type detectDefaultStrategy(final AnnotatedType returnType) {
		Type effectiveType = returnType.getType();

		// Resolve SQLQuery<?, T>
		final Type sqlQueryType = this.findSQLQueryInterface(effectiveType);
		if (sqlQueryType instanceof ParameterizedType) {
			final ParameterizedType sqlParameterizedType = (ParameterizedType) sqlQueryType;
			final Type[] typeArgs = sqlParameterizedType.getActualTypeArguments();
			if (typeArgs.length == 2) {
				effectiveType = typeArgs[1];
			}
		}

		// List<?> -> always LIST_EMPTY
		if (this.isListType(effectiveType)) {
			return Query.Type.LIST_EMPTY;
		}

		final Class<?> effectiveClazz = PCUtils.getRawClass(effectiveType);

		// primitives cannot be null
		if (effectiveClazz.isPrimitive()) {
			return Query.Type.FIRST_THROW;
		}

		// Optional<?> -> FIRST_NULL
		// Nullable annotations -> FIRST_NULL
		if (Optional.class == effectiveClazz || this.isNullable(returnType)) {
			return Query.Type.FIRST_NULL;
		}

		// Non-null annotations -> FIRST_THROW
		if (this.isNonNull(returnType)) {
			return Query.Type.FIRST_THROW;
		}

		return Query.Type.FIRST_NULL;
	}

	/*
	 * this is needed because if the method doesn't have a visibility modifier, the annotations get
	 * applied to the return type
	 */
	public Query.Type detectDefaultStrategy(final AnnotatedType returnType, final AnnotatedElement parentElement) {
		Type effectiveType = returnType.getType();

		// Resolve SQLQuery<?, T>
		final Type sqlQueryType = this.findSQLQueryInterface(effectiveType);
		if (sqlQueryType instanceof ParameterizedType) {
			final ParameterizedType sqlParameterizedType = (ParameterizedType) sqlQueryType;
			final Type[] typeArgs = sqlParameterizedType.getActualTypeArguments();
			if (typeArgs.length == 2) {
				effectiveType = typeArgs[1];
			}
		}

		// List<?> -> always LIST_EMPTY
		if (this.isListType(effectiveType)) {
			return Query.Type.LIST_EMPTY;
		}

		final Class<?> effectiveClazz = PCUtils.getRawClass(effectiveType);

		// primitives cannot be null
		if (effectiveClazz.isPrimitive()) {
			return Query.Type.FIRST_THROW;
		}

		// Optional<?> -> FIRST_NULL
		// Nullable annotations -> FIRST_NULL
		if (Optional.class == effectiveClazz || this.isNullable(returnType) || this.isNullable(parentElement)) {
			return Query.Type.FIRST_NULL;
		}

		// Non-null annotations -> FIRST_THROW
		if (this.isNonNull(returnType) || this.isNonNull(parentElement)) {
			return Query.Type.FIRST_THROW;
		}

		return Query.Type.FIRST_NULL;
	}

	@Override
	public <T extends DatabaseEntry, V> Function<Object[], V>
			buildMethodQueryFunction(final SQLQueryable<T> instance, final Method method, final Map<String, Object> customHints) {
		final QueryStructure queryStructure;

		try {
			if (!method.isAnnotationPresent(Query.class)) {
				throw new IllegalArgumentException("No @Query found on method: " + method);
			}

			queryStructure = this.buildQueryStructure(instance, customHints, method);
		} catch (final Exception e) {
			throw new DBException("Exception when building method query function for:\n" + method + "\non:\n" + instance.getStructure(),
					null,
					new HintsStructure(customHints),
					e);
		}

		try {
			return this.buildQueryMethod(instance, method, queryStructure);
		} catch (final Exception e) {
			throw new DBException("Exception when building method query function for:\n" + method + "\non:\n" + instance.getStructure(),
					null,
					queryStructure,
					e);
		}
	}

	private <T extends DatabaseEntry, B> Function<Object[], B>
			buildQueryMethod(final SQLQueryable<T> instance, final Method method, final QueryStructure queryStructure) {
		final ReturnMapping returnMapping = queryStructure.getReturnMapping();
		final Class<?> returnTypeClass = PCUtils.wrapPrimitiveClass(PCUtils.getRawClass(returnMapping.getActualType().getType()));
		final Query.Type strategy = queryStructure.getStrategy();
		final int[] reordering = queryStructure.getParameterOrder();
		final ColumnType<Object, ?>[] types = Arrays.stream(queryStructure.getParameters())
				.map(QueryParameterPart::getType)
				.toArray(ColumnType[]::new);

		if (queryStructure.isRequireSqlRecompute()) {
			if (returnMapping.isEntryReturn()) {
				final SQLQueryable<?> entryTypeOwner;
				if (returnMapping.getReturnTypeOwnerRef() == null) {
					entryTypeOwner = instance;
				} else {
					entryTypeOwner = this.databaseEntryUtils.getDatabaseScanner()
							.getInstanceFor(returnMapping.getReturnTypeOwnerRef().getKey(),
									returnMapping.getReturnTypeOwnerRef().getValue());
				}

				if (returnTypeClass == Optional.class) {
					return (Function<Object[], B>) objs -> {
						final String sql = this.databaseEntryUtils.getStructureVisitor().buildQuerySql(instance, objs, queryStructure);
						final Object d = instance.query(new EntryTransformingQuery(sql, types, objs, strategy, reordering, entryTypeOwner));
						return (B) returnTypeClass.cast(strategy.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
					};
				} else {
					return (Function<Object[], B>) objs -> {
						final String sql = this.databaseEntryUtils.getStructureVisitor().buildQuerySql(instance, objs, queryStructure);
						return (B) returnTypeClass
								.cast(instance.query(new EntryTransformingQuery(sql, types, objs, strategy, reordering, entryTypeOwner)));
					};
				}
			} else if (returnTypeClass == Optional.class) {
				return (Function<Object[], B>) objs -> {
					final String sql = this.databaseEntryUtils.getStructureVisitor().buildQuerySql(instance, objs, queryStructure);
					final Object d = instance.query(new ScalarTransformingQuery(sql,
							types,
							objs,
							strategy,
							reordering,
							returnMapping.getColumnType(),
							returnMapping.getActualType().getType()));
					return (B) returnTypeClass.cast(strategy.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<Object[], B>) objs -> {
					final String sql = this.databaseEntryUtils.getStructureVisitor().buildQuerySql(instance, objs, queryStructure);
					final Object d = instance.query(new ScalarTransformingQuery(sql,
							types,
							objs,
							strategy,
							reordering,
							returnMapping.getColumnType(),
							returnMapping.getActualType().getType()));
					return (B) returnTypeClass.cast(d);
				};
			}
		} else {
			final String sql = queryStructure.getSql();
			Objects.requireNonNull(sql, "SQL is null.");
			if (returnMapping.isEntryReturn()) {
				final SQLQueryable<?> entryTypeOwner;
				if (returnMapping.getReturnTypeOwnerRef() == null) {
					entryTypeOwner = instance;
				} else {
					entryTypeOwner = this.databaseEntryUtils.getDatabaseScanner()
							.getInstanceFor(returnMapping.getReturnTypeOwnerRef().getKey(),
									returnMapping.getReturnTypeOwnerRef().getValue());
				}

				if (returnTypeClass == Optional.class) {
					return (Function<Object[], B>) objs -> {
						final Object d = instance.query(new EntryTransformingQuery(sql, types, objs, strategy, reordering, entryTypeOwner));
						return (B) returnTypeClass.cast(strategy.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
					};
				} else {
					return (Function<Object[], B>) objs -> (B) returnTypeClass
							.cast(instance.query(new EntryTransformingQuery(sql, types, objs, strategy, reordering, entryTypeOwner)));
				}
			} else if (returnTypeClass == Optional.class) {
				return (Function<Object[], B>) objs -> {
					final Object d = instance.query(new ScalarTransformingQuery(sql,
							types,
							objs,
							strategy,
							reordering,
							returnMapping.getColumnType(),
							returnMapping.getActualType().getType()));
					return (B) returnTypeClass.cast(strategy.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<Object[], B>) objs -> (B) returnTypeClass.cast(instance.query(new ScalarTransformingQuery<>(sql,
						types,
						objs,
						strategy,
						reordering,
						returnMapping.getColumnType(),
						returnMapping.getActualType().getType())));
			}
		}
	}

	protected Type findSQLQueryInterface(final Type type) {
		if (!(type instanceof ParameterizedType)) {
			return null;
		}

		final ParameterizedType pt = (ParameterizedType) type;
		final Class<?> rawClass = (Class<?>) pt.getRawType();

		for (final Type iface : rawClass.getGenericInterfaces()) {
			if (iface instanceof ParameterizedType) {
				final ParameterizedType ipt = (ParameterizedType) iface;
				final Type rawIface = ipt.getRawType();
				if (rawIface instanceof Class<?> && SQLQuery.class.isAssignableFrom((Class<?>) rawIface)) {
					return ipt;
				}
			}
		}

		final Type superType = rawClass.getGenericSuperclass();
		if (superType != null) {
			return this.findSQLQueryInterface(superType);
		}

		return null;
	}

	private ReturnMapping buildReturnMapping(final SQLQueryable<?> instance, final ViewTableStructure[] tablesArr, final Method method) {
		final AnnotatedType annotatedType = method.getAnnotatedReturnType();
		final AnnotatedType containedType = this.getActualReturnType(annotatedType);
		final Class<?> actualRawType = PCUtils.getRawClass(containedType.getType());
		final boolean entryReturn = DatabaseEntry.class.isAssignableFrom(actualRawType);

		final ColumnType<?, ?> columnType;
		ReadOnlyPair<Class<? extends SQLQueryable<?>>, String> returnTypeOwnerRef = null;
		if (entryReturn) {
			columnType = null;

			if (!instance.getEntryClass().isAssignableFrom(actualRawType)) {
				for (final ViewTableStructure table : tablesArr) {
					if (this.databaseEntryUtils.getDatabaseScanner()
							.getStructureFor(table.getForeignClass(), table.getForeignName())
							.getEntryClass()
							.equals(actualRawType)) {
						returnTypeOwnerRef = Pairs.readOnly(table.getForeignClass(), table.getForeignName());
						break;
					}
				}

				if (returnTypeOwnerRef == null) {
					throw new IllegalArgumentException("Return type: " + actualRawType + " (from: " + annotatedType
							+ ") doesn't match any DatabaseEntryType used in the current query:\n * " + instance.getStructure() + "\n"
							+ Arrays.stream(tablesArr)
									.map(c -> " * " + this.databaseEntryUtils.getDatabaseScanner()
											.getInstanceFor(c.getForeignClass(), c.getForeignName())
											.getStructure())
									.collect(Collectors.joining("\n")));
				}
			}
		} else {
			columnType = this.databaseEntryUtils.getTypeFor(containedType);
			returnTypeOwnerRef = null;
		}

		return new ReturnMapping(annotatedType, entryReturn, columnType, returnTypeOwnerRef);
	}

	private AnnotatedType getActualReturnType(final AnnotatedType type) {
		if (type instanceof AnnotatedParameterizedType) {
			final Type rawType = ((ParameterizedType) type.getType()).getRawType();
			final AnnotatedType[] args = ((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments();

			if ((Optional.class.equals(rawType) || List.class.equals(rawType)) && args.length == 1) {
				return this.getActualReturnType(args[0]);
			}

			if (NextTask.class.equals(rawType) && args.length > 0) {
				return this.getActualReturnType(args[args.length - 1]);
			}
		}
		return type;
	}

	private boolean isNonNull(final AnnotatedElement annotatedElement) {
		return Arrays.stream(annotatedElement.getAnnotations())
				.anyMatch(
						c -> "NotNull".equals(c.annotationType().getSimpleName()) || "NonNull".equals(c.annotationType().getSimpleName()));
	}

	private boolean isNullable(final AnnotatedElement annotatedElement) {
		return Arrays.stream(annotatedElement.getAnnotations()).anyMatch(c -> "Nullable".equals(c.annotationType().getSimpleName()));
	}

	private String normalizeComparator(final String comparator, final Method method) {
		final String normalized = comparator == null ? "=" : comparator.trim().toUpperCase();
		switch (normalized) {
		case "=":
		case "<":
		case "<=":
		case ">":
		case ">=":
		case "LIKE":
		case "<>":
			return normalized;
		default:
			throw new IllegalArgumentException("Unsupported @Param comparator '" + comparator + "' on method " + method
					+ ".\nSupported comparators are: =, <, <=, >, >=, <>, !=, LIKE.");
		}
	}

	private String resolveParameterColumnName(
			final SQLQueryable<?> instance,
			final Parameter parameter,
			final Map<String, Object> hints,
			final ViewTableStructure[] tablesArr,
			final Method method) {
		if (PCUtils.nullIfBlank((String) hints.get(DefaultQueryHints.PARAM_NAME)) != null) {
			return this.databaseEntryUtils.resolveSQLQualifiers(instance,
					PCUtils.nullIfBlank((String) hints.get(DefaultQueryHints.PARAM_NAME)).trim(),
					new HashMap<>(),
					token -> {
						if (token.startsWith(DatabaseEntryUtils.ALIAS_KEY)) {
							final String replacement;
							final String[] tokens = token.split(":");
							switch (tokens.length) {
							case 2: {
								final SQLQueryableStructure foreignStructure = instance.getDatabase()
										.getStructure()
										.getSimpleName(tokens[1]);
								if (foreignStructure == null) {
									throw new NoMatchingStructureException(
											"No SQLQueryable found bound to name: '" + tokens[1]
													+ "', use @DefinedName(...) or use the simple class name.",
											null,
											instance.getDatabase().getStructure());
								}
								final ViewTableStructure vts = this
										.getStructureMatching(instance.getStructure(), foreignStructure, tablesArr);
								replacement = vts.getAlias();
								break;
							}
							case 3: {
								final Map<String, SQLQueryableStructure> foreignStructures = instance.getDatabase()
										.getStructure()
										.getLinkedNames()
										.get(tokens[1]);
								if (foreignStructures == null) {
									throw new NoMatchingStructureException("No SQLQueryable found bound to simple class name: '" + tokens[1]
											+ "'.", null, instance.getDatabase().getStructure());
								}
								final SQLQueryableStructure foreignStructure = foreignStructures.get(tokens[2]);
								if (foreignStructure == null) {
									throw new NoMatchingStructureException("No SQLQueryable found bound to simple class name: '" + tokens[1]
											+ "' and name override: '" + tokens[2] + "'.", null, instance.getDatabase().getStructure());
								}
								final ViewTableStructure vts = this
										.getStructureMatching(instance.getStructure(), foreignStructure, tablesArr);
								replacement = vts.getAlias();
								break;
							}
							default:
								throw new InvalidPlaceholderException(
										"Invalid input: '" + token + "', expected one of:\n * fieldName\n * simpleClassName:fieldName\n"
												+ " * definedName:fieldName\n * simpleClassName:nameOverride:fieldName");
							}

							return Optional.of(replacement);
						}

						return Optional.empty();
					});
		}

		if (PCUtils.nullIfBlank((String) hints.get(DefaultQueryHints.PARAM_MEMBER_NAME)) != null) {
			return this.databaseEntryUtils.resolveSQLQualifiers(instance,
					String.format("{%s%s}", DatabaseEntryUtils.MEMBER_KEY, hints.get(DefaultQueryHints.PARAM_MEMBER_NAME)));
		}

		final String name = parameter.getName();
		if (!parameter.isNamePresent() || name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Could not resolve query column name for parameter " + parameter + " on method " + method
					+ ". Add @Param(\"column_name\") to the parameter.");
		}

		return this.structureVisitor.qualifiedName(this.structureVisitor.memberToColumnName(name.trim()));
	}

	private ViewTableStructure getStructureMatching(
			final SQLQueryableStructure instanceStructure,
			final SQLQueryableStructure foreignStructure,
			final ViewTableStructure[] tablesArr) {
		for (final ViewTableStructure vts : tablesArr) {
			if (vts.getForeignClass() == foreignStructure.getTargetClass()
					&& vts.getResolvedName().getQualifiedName().equals(foreignStructure.getStructureName().getQualifiedName())) {
				return vts;
			}
		}

		throw new IllegalArgumentException("Structure: " + foreignStructure.toString()
				+ " ViewTableStructure used in the current query:\n * " + instanceStructure + "\n"
				+ Arrays.stream(tablesArr)
						.map(c -> " * " + this.databaseEntryUtils.getDatabaseScanner()
								.getInstanceFor(c.getForeignClass(), c.getForeignName())
								.getStructure())
						.collect(Collectors.joining("\n")));
	}

	protected boolean isListType(final Type type) {
		if (type instanceof ParameterizedType) {
			final Type raw = ((ParameterizedType) type).getRawType();
			if (raw instanceof Class<?>) {
				return Collection.class.isAssignableFrom((Class<?>) raw);
			}
		}
		if (type instanceof Class<?>) {
			return Collection.class.isAssignableFrom((Class<?>) type);
		}
		return false;
	}

	private <T extends DatabaseEntry> QueryStructure
			buildQueryStructure(final SQLQueryable<T> instance, final Map<String, Object> hints, final Method method) {
		final Map<String, Object> hs = this.databaseEntryUtils.getHintScanner().computeQueryHints(method);
		hs.putAll(hints);
		hints.clear();
		hints.putAll(hs);

		String customSQL = PCUtils.nullIfBlank((String) hints.get(DefaultQueryHints.CUSTOM_SQL));

		final String[] columns;
		if (hints.containsKey(DefaultQueryHints.COLUMNS)) {
			columns = Arrays.stream((String[]) hints.get(DefaultQueryHints.COLUMNS))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(c -> this.databaseEntryUtils.resolveSQLQualifiers(instance, c))
					.toArray(String[]::new);
		} else {
			columns = new String[] { "*" };
		}

		final String[] retColumns = Arrays.stream((String[]) hints.get(DefaultQueryHints.RETURN_COLUMNS))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(c -> this.databaseEntryUtils.resolveSQLQualifiers(instance, c))
				.toArray(String[]::new);
		final boolean distinct = (boolean) hints.getOrDefault(DefaultQueryHints.DISTINCT, false);

		final DatabaseScanner scanner = this.databaseEntryUtils.getDatabaseScanner();

		final List<ViewTableStructure> tables = new ArrayList<>();
//		char aliasChar = 'A';
		if (hints.containsKey(DefaultQueryHints.TABLES)) {
			for (final Map<String, Object> table : (List<Map<String, Object>>) hints.get(DefaultQueryHints.TABLES)) {
				final ViewTableStructure tt = scanner.buildTable(instance, table);
//				if (tt.getAlias() == null) {
//					tt.setAlias(this.databaseEntryUtils.getStructureVisitor().qualifiedName("_" + aliasChar));
//					aliasChar++;
//					if (aliasChar > 'Z') {
//						aliasChar = 'a';
//					}
//				}
				tables.add(tt);
			}
		}
		final ViewTableStructure[] tablesArr = tables.toArray(new ViewTableStructure[0]);

		final ViewTableStructure mainTable = new ViewTableStructure(instance
				.getName(), instance.getTargetClass(), instance.getStructure().getStructureName(), null, null, Table.Type.MAIN, distinct);
		tables.add(mainTable);
		scanner.resolveMissingJoinConditions(tables);

		final ReturnMapping returnMapping = this.buildReturnMapping(instance, tablesArr, method);
		if (returnMapping.isEntryReturn() && tablesArr.length > 0 && retColumns.length == 1 && "*".equals(retColumns[0])) {
			final SQLQueryable<?> returnTypeOwner;
			if (returnMapping.getReturnTypeOwnerRef() == null) {
				returnTypeOwner = instance;
			} else {
				returnTypeOwner = scanner.getInstanceFor(returnMapping.getReturnTypeOwnerRef().getKey(),
						returnMapping.getReturnTypeOwnerRef().getValue());
			}
			retColumns[0] = returnTypeOwner.getQualifiedName() + ".*";
		}

		final List<ViewOrderStructure> orderBys = new ArrayList<>();
		if (hints.containsKey(DefaultQueryHints.ORDER_BY)) {
			for (final Map<String, Object> orderBy : (List<Map<String, Object>>) hints.get(DefaultQueryHints.ORDER_BY)) {
				orderBys.add(scanner.buildOrderBy(instance, orderBy));
			}
		}
		final ViewOrderStructure[] orderByArr = orderBys.toArray(new ViewOrderStructure[0]);

		final String condition = PCUtils.nullIfBlank((String) hints.get(DefaultQueryHints.CONDITION));

		boolean foundLimit = false;
		boolean foundOffset = false;
		boolean hasIgnoreNull = false;
		boolean requiredSqlRecompute = false;
		final int limitId = -1;
		final int offsetId = -1;
		final QueryParameterPart[] parameters = new QueryParameterPart[method.getParameterCount()];
		for (final Map<String, Object> paramHints : (List<Map<String, Object>>) hints.get(DefaultQueryHints.PARAMETERS)) {
			final int index = (int) paramHints.get(DefaultQueryHints.PARAM_INDEX);
			final Parameter parameter = method.getParameters()[index];
			final ColumnType<?, ?> columnType = this.getTypeFor(instance, paramHints, tablesArr, parameter);
			final boolean entry = (boolean) paramHints.get(DefaultQueryHints.PARAM_ENTRY);
			final boolean list = (boolean) paramHints.get(DefaultQueryHints.PARAM_COLLECTION);
			final String column = list && entry || entry ? null
					: this.resolveParameterColumnName(instance, parameter, paramHints, tablesArr, method);

			if (list && !entry) {
				paramHints.put(DefaultQueryHints.PARAM_COLUMNS, new String[] { column });
			}

			parameters[index] = new QueryParameterPart(index,
					parameter.getName(),
					column,
					this.normalizeComparator((String) paramHints.getOrDefault(DefaultQueryHints.PARAM_COMPARATOR, "="), method),
					this.parseBoolean(paramHints.get(DefaultQueryHints.PARAM_IGNORE_NULL), false),
					this.parseBoolean(paramHints.get(DefaultQueryHints.PARAM_LIMIT), false),
					this.parseBoolean(paramHints.get(DefaultQueryHints.PARAM_OFFSET), false),
					list,
					list || entry ? (String[]) paramHints.get(DefaultQueryHints.PARAM_COLUMNS) : new String[0],
					entry,
					columnType);

			if ((parameters[index].isLimit() || parameters[index].isOffset()) && paramHints.containsKey(DefaultQueryHints.PARAM_PARAM)) {
				throw new IllegalArgumentException("@Limit/@Offset cannot be combined with @Param.");
			}

			if (parameters[index].isLimit()) {
				if (foundLimit) {
					throw new IllegalArgumentException("@Limit present more than once.");
				} else {
					foundLimit = true;
				}
			}

			if (parameters[index].isOffset()) {
				if (foundOffset) {
					throw new IllegalArgumentException("@Offset present more than once.");
				} else {
					foundOffset = true;
				}
			}

			if (parameters[index].isIgnoreNull()) {
				hasIgnoreNull = true;
			}

			if (parameters[index].isList()) {
				requiredSqlRecompute = true;
			}
		}

		final List<Integer> paramOrder = new ArrayList<>();
		if (customSQL != null) {
			final Map<String, String> paramNameToColumnName = new HashMap<>();
			final Map<String, Integer> paramNameToIndex = new HashMap<>();
			for (int i = 0; i < parameters.length; i++) {
				final QueryParameterPart part = parameters[i];
				paramNameToColumnName.put(Integer.toString(i), part.getColumn());
				paramNameToColumnName.put(part.getParameterName(), part.getColumn());

				paramNameToIndex.put(Integer.toString(i), i);
				paramNameToIndex.put(part.getParameterName(), i);
			}

			customSQL = this.databaseEntryUtils.resolveSQLQualifiers(instance, customSQL, new HashMap<>(), in -> {
				if (in.startsWith(DatabaseEntryUtils.PARAMETER_COLUMN_KEY)) {
					final String[] tokens = in.split(":");
					if (paramNameToColumnName.containsKey(tokens[1])) {
						return Optional.ofNullable(paramNameToColumnName.get(tokens[1]));
					} else {
						throw new IllegalArgumentException("Parameter named: '" + tokens[1] + "' not found on " + method
								+ "\nYou may need to enable parameter name retention during compilation.");
					}
				} else if (in.startsWith(DatabaseEntryUtils.PARAMETER_VALUE_KEY)) {
					final String[] tokens = in.split(":");
					if (paramNameToIndex.containsKey(tokens[1])) {
						paramOrder.add(paramNameToIndex.get(tokens[1]));
						return Optional.of("?");
					} else {
						throw new IllegalArgumentException("Parameter named: '" + tokens[1] + "' not found on " + method
								+ "\nYou may need to enable parameter name retention during compilation.");
					}
				}

				return Optional.empty();
			});
		}

		if (customSQL == null || paramOrder.isEmpty()) {
			IntStream.range(0, parameters.length).forEachOrdered(paramOrder::add);
		}

		if (hasIgnoreNull && customSQL == null) {
			for (final QueryParameterPart part : parameters) {
				if (!part.isIgnoreNull()) {
					continue;
				}

				final int index = part.getIndex();

				for (int i = paramOrder.size() - 1; i >= 0; i--) {
					if (paramOrder.get(i) == index) {
						paramOrder.add(i + 1, index);
					}
				}
			}
		}

		if (limitId >= 0) {
			paramOrder.remove(Integer.valueOf(limitId));
			paramOrder.add(limitId);
		}

		if (offsetId >= 0) {
			paramOrder.remove(Integer.valueOf(offsetId));
			paramOrder.add(offsetId);
		}

		Query.Type type = (Query.Type) hints.getOrDefault(DefaultQueryHints.STRATEGY, Query.Type.AUTO);
		if (type == Query.Type.AUTO) {
			type = this.detectDefaultStrategy(method.getAnnotatedReturnType(), method);
		}

		final String asName = PCUtils.nullIfBlank((String) hints.get(DefaultQueryHints.AS_NAME));
		final @Qualified String qualifiedAsName = asName == null ? null : this.structureVisitor.qualifiedName(asName);

		final QueryStructure structure = new QueryStructure(instance.getQualifiedName(),
				qualifiedAsName,
				columns,
				retColumns,
				tablesArr,
				condition,
				orderByArr,
				customSQL,
				type,
				parameters,
				hints,
				returnMapping,
				distinct,
				foundLimit,
				foundOffset,
				requiredSqlRecompute,
				paramOrder.stream().mapToInt(Integer::intValue).toArray());

		if (customSQL != null) {
			structure.setSql(customSQL);
		} else if (!requiredSqlRecompute) {
			final String sql = this.structureVisitor.buildQuerySql(instance, null, structure);
			structure.setSql(sql);
		}

		return structure;
	}

	private ColumnType<?, ?> getTypeFor(
			final SQLQueryable<?> instance,
			final Map<String, Object> paramHints,
			final ViewTableStructure[] tablesArr,
			final Parameter parameter) {

		final AnnotatedType type = parameter.getAnnotatedType();
		final Type genericType = type.getType();

		// DatabaseEntry or subclass
		if (genericType instanceof Class && DatabaseEntry.class.isAssignableFrom((Class<?>) genericType)) {

			paramHints.put(DefaultQueryHints.PARAM_ENTRY, true);
			paramHints.put(DefaultQueryHints.PARAM_COLLECTION, false);

			final ReadOnlyPair<SQLQueryableStructure, ViewTableStructure> s = this.getStructure(instance.getStructure(), tablesArr, type);
			final PrimaryKeyColumnType<?> pkColumn = new PrimaryKeyColumnType<>(s.getKey());

			paramHints.put(DefaultQueryHints.PARAM_COLUMNS,
					Arrays.stream(pkColumn.getPrimaryKeys()).map(c -> c.getQualifiedName()).toArray(String[]::new));

			return pkColumn;
		}

		// Collection<T>
		if (genericType instanceof ParameterizedType && type instanceof AnnotatedParameterizedType) {
			final ParameterizedType parameterizedType = (ParameterizedType) genericType;
			final Type rawType = parameterizedType.getRawType();

			if (rawType instanceof Class && Collection.class.isAssignableFrom((Class<?>) rawType)) {
				final Type[] typeArguments = parameterizedType.getActualTypeArguments();
				final AnnotatedType[] annotatedTypeArguments = ((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments();

				if (typeArguments.length == 1 && annotatedTypeArguments.length == 1) {
					final Type elementType = typeArguments[0];
					final AnnotatedType annotatedElementType = annotatedTypeArguments[0];

					paramHints.put(DefaultQueryHints.PARAM_COLLECTION, true);

					// Collection<DatabaseEntry> or Collection<DatabaseEntrySubclass>
					if (elementType instanceof Class && DatabaseEntry.class.isAssignableFrom((Class<?>) elementType)) {

						paramHints.put(DefaultQueryHints.PARAM_ENTRY, true);

						final ReadOnlyPair<SQLQueryableStructure, ViewTableStructure> s = this
								.getStructure(instance.getStructure(), tablesArr, annotatedElementType);
						final PrimaryKeyColumnType<?> pkColumn = new PrimaryKeyColumnType<>(s.getKey());

						paramHints.put(DefaultQueryHints.PARAM_COLUMNS,
								Arrays.stream(pkColumn.getPrimaryKeys())
										.map(c -> s.hasValue() ? s.getValue().getAlias() + "." + c.getLocalQualifiedName()
												: c.getQualifiedName())
										.toArray(String[]::new));

						return new DelegatingCollectionColumnType<>(pkColumn);
					}

					// Collection of something else
					paramHints.put(DefaultQueryHints.PARAM_ENTRY, false);

					return new DelegatingCollectionColumnType<>(this.databaseEntryUtils.getTypeFor(annotatedElementType));
				}
			}
		}

		// Array<T>
		if (genericType instanceof Class && ((Class<?>) genericType).isArray()) {
			final Class<?> elementType = ((Class<?>) genericType).getComponentType();

			paramHints.put(DefaultQueryHints.PARAM_COLLECTION, true);

			// DatabaseEntry[] or DatabaseEntrySubclass[]
			if (DatabaseEntry.class.isAssignableFrom(elementType)) {
				paramHints.put(DefaultQueryHints.PARAM_ENTRY, true);

				final AnnotatedType annotatedElementType = type instanceof AnnotatedArrayType
						? ((AnnotatedArrayType) type).getAnnotatedGenericComponentType()
						: type;
				final ReadOnlyPair<SQLQueryableStructure, ViewTableStructure> s = this
						.getStructure(instance.getStructure(), tablesArr, annotatedElementType);
				final PrimaryKeyColumnType<?> pkColumn = new PrimaryKeyColumnType<>(s.getKey());

				paramHints.put(DefaultQueryHints.PARAM_COLUMNS,
						Arrays.stream(pkColumn.getPrimaryKeys())
								.map(c -> s.hasValue() ? s.getValue().getAlias() + "." + c.getLocalQualifiedName() : c.getQualifiedName())
								.toArray(String[]::new));

				return new DelegatingArrayColumnType<>(pkColumn);
			}

			// Array of something else
			paramHints.put(DefaultQueryHints.PARAM_ENTRY, false);

			final AnnotatedType annotatedElementType = type instanceof AnnotatedArrayType
					? ((AnnotatedArrayType) type).getAnnotatedGenericComponentType()
					: type;

			return new DelegatingArrayColumnType<>(this.databaseEntryUtils.getTypeFor(annotatedElementType));
		}

		// Fallback
		paramHints.put(DefaultQueryHints.PARAM_ENTRY, false);
		paramHints.put(DefaultQueryHints.PARAM_COLLECTION, false);

		return this.databaseEntryUtils.getTypeFor(type);
	}

	private ReadOnlyPair<SQLQueryableStructure, ViewTableStructure> getStructure(
			final SQLQueryableStructure instanceStructure,
			final ViewTableStructure[] tablesArr,
			final AnnotatedType annotatedType) {
		final Class<?> actualRawType = PCUtils.getRawClass(annotatedType.getType());

		if (!instanceStructure.getEntryClass().isAssignableFrom(actualRawType)) {
			for (final ViewTableStructure table : tablesArr) {
				final SQLQueryableStructure struct = this.databaseEntryUtils.getDatabaseScanner()
						.getStructureFor(table.getForeignClass(), table.getForeignName());
				if (struct.getEntryClass().equals(actualRawType)) {
					return Pairs.readOnly(struct, table);
				}
			}

			throw new IllegalArgumentException("Type: " + actualRawType + " (from: " + annotatedType
					+ ") doesn't match any DatabaseEntryType used in the current query:\n * " + instanceStructure + "\n"
					+ Arrays.stream(tablesArr)
							.map(c -> " * " + this.databaseEntryUtils.getDatabaseScanner()
									.getInstanceFor(c.getForeignClass(), c.getForeignName())
									.getStructure())
							.collect(Collectors.joining("\n")));
		}

		return Pairs.readOnly(instanceStructure, null);
	}

	private boolean parseBoolean(final Object object, final boolean b) {
		if (object instanceof String) {
			return PCUtils.parseBoolean((String) object, b);
		}
		if (object instanceof Boolean) {
			return (Boolean) object;
		}
		if (object == null) {
			return false;
		}
		if (object.getClass() == boolean.class) {
			return (boolean) object;
		}
		if (object instanceof Number) {
			return ((Number) object).longValue() != 0;
		}
		if (object.getClass() == int.class) {
			return (int) object != 0;
		}
		return true;
	}

}
