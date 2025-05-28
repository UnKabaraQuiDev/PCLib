package lu.pcy113.pclib.db.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Generated;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKey;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.entry.Reload;
import lu.pcy113.pclib.db.annotations.entry.UniqueKey;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.BooleanType;
import lu.pcy113.pclib.db.autobuild.column.ColumnData;
import lu.pcy113.pclib.db.autobuild.column.ColumnType;
import lu.pcy113.pclib.db.autobuild.column.DecimalTypes.DoubleType;
import lu.pcy113.pclib.db.autobuild.column.DecimalTypes.FloatType;
import lu.pcy113.pclib.db.autobuild.column.ForeignKey;
import lu.pcy113.pclib.db.autobuild.column.IntTypes.BigIntType;
import lu.pcy113.pclib.db.autobuild.column.IntTypes.IntType;
import lu.pcy113.pclib.db.autobuild.column.IntTypes.SmallIntType;
import lu.pcy113.pclib.db.autobuild.column.NColumn;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.TextTypes.TextType;
import lu.pcy113.pclib.db.autobuild.column.TextTypes.VarcharType;
import lu.pcy113.pclib.db.autobuild.column.TimeTypes.DateType;
import lu.pcy113.pclib.db.autobuild.column.TimeTypes.TimestampType;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.autobuild.table.ConstraintData;
import lu.pcy113.pclib.db.autobuild.table.PrimaryKeyData;
import lu.pcy113.pclib.db.autobuild.table.TableStructure;
import lu.pcy113.pclib.db.autobuild.table.UniqueData;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public class BaseSQLEntryUtils implements SQLEntryUtils {

	@Override
	public <T extends SQLEntry> void generatedKeyUpdate(T data, ResultSet rs) {
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(GeneratedKeyUpdate.class)) {
				final GeneratedKeyUpdate generatedKeyUpdate = m.getAnnotation(GeneratedKeyUpdate.class);
				try {
					if (generatedKeyUpdate.type().equals(GeneratedKeyUpdate.Type.RESULT_SET)) {
						m.invoke(data, rs);
					} else if (generatedKeyUpdate.type().equals(GeneratedKeyUpdate.Type.INDEX)) {
						m.invoke(data, m.getParameterTypes()[0].cast(rs.getObject(generatedKeyUpdate.index())));
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
					throw new RuntimeException(e);
				}
				break;
			}
		}
	}

	@Override
	public <T extends SQLEntry> void reload(T data, ResultSet rs) {
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				try {
					m.invoke(data, rs);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				break;
			}
		}
	}

	@Override
	public <T extends SQLEntry> String getGeneratedKeyName(T data) {
		return data.getClass().getAnnotation(GeneratedKey.class).value();
	}

	@Override
	public <T extends SQLEntry> T copy(T data, ResultSet rs) {
		data = instance(data);
		reload(data, rs);
		return data;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SQLEntry> T instance(T data) {
		return (T) instance(data.getClass());
	}

	@Override
	public <T extends SQLEntry> T instance(Class<T> clazz) {
		try {
			final Constructor<T> constr = clazz.getDeclaredConstructor();
			constr.setAccessible(true);
			return constr.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException {
		Method reloadMethod = null;
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				reloadMethod = m;
				break;
			}
		}
		if (reloadMethod == null) {
			throw new IllegalStateException("No method annotated with @Reload found");
		}

		while (result.next()) {
			T newData = instance(data);
			try {
				reloadMethod.invoke(newData, result);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			listExporter.accept(newData);
		}
	}

	@Override
	public <T extends SQLQuery<B>, B extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<B> listExporter) throws SQLException {
		Method reloadMethod = null;
		for (Method m : data.clone().getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				reloadMethod = m;
				break;
			}
		}
		if (reloadMethod == null) {
			throw new IllegalStateException("No method annotated with @Reload found");
		}

		while (result.next()) {
			B newData = (B) data.clone();
			try {
				reloadMethod.invoke(newData, result);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			listExporter.accept(newData);
		}
	}

	@Override
	public <T extends SQLEntry> Map<String, Object>[] getUniqueKeys(Constraint[] allConstraints, T data) {
		final List<Constraint> uniqueConstraints = Arrays.stream(allConstraints).filter((Constraint c) -> c.type().equals(Constraint.Type.UNIQUE)).collect(Collectors.toList());

		final Set<String> declaredUniquesSet = new HashSet<>();
		Arrays.stream(allConstraints).filter((Constraint c) -> c.type().equals(Constraint.Type.UNIQUE)).map(Constraint::columns).flatMap(Arrays::stream).forEach(declaredUniquesSet::add);

		if (declaredUniquesSet.size() == 0) {
			return null;
		}

		final Map<String, Object> uniqueValues = new HashMap<>();

		try {
			for (Method m : data.getClass().getMethods()) {
				if (m.isAnnotationPresent(UniqueKey.class)) {
					final UniqueKey uniqueValue = m.getAnnotation(UniqueKey.class);

					if (declaredUniquesSet.contains(uniqueValue.value())) {
						uniqueValues.put(uniqueValue.value(), m.invoke(data));
						declaredUniquesSet.remove(uniqueValue.value());
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		if (declaredUniquesSet.size() > 0) {
			throw new IllegalStateException("Missing unique keys: " + declaredUniquesSet);
		}

		final Map<String, Object>[] uniques = new HashMap[uniqueConstraints.size()];

		for (int i = 0; i < uniqueConstraints.size(); i++) {
			final Constraint constraint = uniqueConstraints.get(i);
			uniques[i] = new HashMap<String, Object>();

			for (String key : constraint.columns()) {
				uniques[i].put(key, uniqueValues.get(key));
			}
		}

		// remove null values and empty maps
		final List<Map<String, Object>> uniques2 = Arrays.stream(uniques).map(unique -> unique.entrySet().stream().filter(c -> c.getValue() != null).collect(Collectors.toMap(Entry::getKey, Entry::getValue))).filter(c -> !c.isEmpty())
				.collect(Collectors.toList());

		return uniques2.toArray(new HashMap[uniques2.size()]);
	}

	@Override
	public <T extends SQLEntry> TableStructure getColumns(Class<T> clazz) {
		System.out.println("Scanning class: " + clazz.getSimpleName());

		List<ColumnData> columns = new ArrayList<>();
		Map<Integer, Set<String>> uniqueColumns = new HashMap<>();
		List<ForeignKey> fks = new ArrayList<>();

		Set<String> pks = new HashSet<>();
		Set<String> generated = new HashSet<>();

		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true); // Include private fields

			final ColumnData columnData = new ColumnData();

			if (field.isAnnotationPresent(NColumn.class)) {
				NColumn column = field.getAnnotation(NColumn.class);

				columnData.setName(column.name().isEmpty() ? toColumnName(field.getName()) : column.name());
				columnData.setType(getTypeFor(column.type().equals(Class.class) ? field.getType() : column.type(), column));
			} else {
				continue;
			}

			if (field.isAnnotationPresent(PrimaryKey.class)) {
				final PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
				pks.add(columnData.getName());
				System.out.println("Found @PrimaryKey on: " + field.getName());
			}

			if (field.isAnnotationPresent(AutoIncrement.class)) {
				columnData.setAutoIncrement(true);
				System.out.println("Found @AutoIncrement on: " + field.getName());
			}

			if (field.isAnnotationPresent(Generated.class)) {
				System.out.println("Found @Generated on: " + field.getName());
			}

			if (field.isAnnotationPresent(Unique.class)) {
				final Unique unique = field.getAnnotation(Unique.class);
				if (!uniqueColumns.containsKey(unique.value())) {
					uniqueColumns.put(unique.value(), new HashSet<>());
				}
				uniqueColumns.get(unique.value()).add(columnData.getName());
				System.out.println("Found @Unique on: " + field.getName() + " (Group " + unique.value() + ")");
			}

			if (field.isAnnotationPresent(ForeignKey.class)) {
				final ForeignKey fk = field.getAnnotation(ForeignKey.class);
				fks.add(fk);
				System.out.println("Found @ForeignKey on: " + field.getName());
			}

			System.out.println("  ==  == >> " + columnData.build());
		}

		final TableStructure ts = new TableStructure(clazz, columns.toArray(new ColumnData[0]));

		List<ConstraintData> constraints = new ArrayList<>();
		if (pks.size() > 0) {
			constraints.add(new PrimaryKeyData(ts, pks.toArray(new String[0])));
		}
		if (uniqueColumns.size() > 0) {
			for (Entry<Integer, Set<String>> entry : uniqueColumns.entrySet()) {
				constraints.add(new UniqueData(ts, entry.getValue().toArray(new String[0])));
			}
		}
		if (fks.size() > 0) {
			for (ForeignKey fk : fks) {
				constraints.add(new ConstraintData(ts, fk.table(), fk.columns(), fk.referencedTable(), fk.referencedColumns(), fk.onDeleteAction(), fk.onUpdateAction()));
			}
		}

		return null;
	}

	private String toColumnName(String name) {
		return PCUtils.camelToSnake(name);
	}

	private Map<Class<?>, Function<NColumn, ColumnType>> typeMap = new HashMap<Class<?>, Function<NColumn, ColumnType>>() {
		{
			put(String.class, (NColumn col) -> col.length() > 0 && col.length() < 256 ? new VarcharType(col.length()) : new TextType());
			put(CharSequence.class, (NColumn col) -> col.length() > 0 && col.length() < 256 ? new VarcharType(col.length()) : new TextType());

			put(Short.class, (NColumn col) -> new SmallIntType());
			put(short.class, (NColumn col) -> new SmallIntType());
			put(Integer.class, (NColumn col) -> new IntType());
			put(int.class, (NColumn col) -> new IntType());
			put(Long.class, (NColumn col) -> new BigIntType());
			put(long.class, (NColumn col) -> new BigIntType());

			put(Double.class, (NColumn col) -> new DoubleType());
			put(double.class, (NColumn col) -> new DoubleType());
			put(Float.class, (NColumn col) -> new FloatType());
			put(float.class, (NColumn col) -> new FloatType());

			put(Boolean.class, (NColumn col) -> new BooleanType());
			put(boolean.class, (NColumn col) -> new BooleanType());

			put(java.sql.Timestamp.class, (NColumn col) -> new TimestampType());
			put(java.sql.Date.class, (NColumn col) -> new DateType());
		}
	};

	private ColumnType getTypeFor(Class<?> clazz, NColumn col) {
		if (typeMap.containsKey(clazz)) {
			return typeMap.get(clazz).apply(col);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + clazz.getName() + " for column: " + col.name());
		}
	}

}
