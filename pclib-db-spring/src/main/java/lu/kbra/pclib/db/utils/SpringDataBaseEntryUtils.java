package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.type.ListType;
import lu.kbra.pclib.db.view.AbstractDBView;

public class SpringDataBaseEntryUtils extends BaseProxyDataBaseEntryUtils {

	public SpringDataBaseEntryUtils(final ObjectMapper objectMapper, final ConversionService conversionService) {
		appendSpringTypes(objectMapper, conversionService);
	}

	public void registerClassType(final Predicate<Class<?>> k, final Function<Column, ColumnType> v) {
		this.classTypeMap.put(k, v);
	}

	public void registerType(final Class<?> k, final Function<Column, ColumnType> v) {
		this.typeMap.put(k, v);
	}

//	@Autowired
	public void appendSpringTypes(final ObjectMapper objectMapper, final ConversionService conversionService) {
		// java types -----
		this.typeMap.put(List.class, col -> new ListType(objectMapper, conversionService));
		this.typeMap.put(ArrayList.class, col -> new ListType(objectMapper, conversionService));
		this.typeMap.put(LinkedList.class, col -> new ListType(objectMapper, conversionService));

		// native types -----
		this.typeMap.put(ListType.class, col -> new ListType(objectMapper, conversionService));
	}

	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(
			final Class<? extends SQLQueryable<? extends DataBaseEntry>> type) {
		if (SQLQueryable.class.isAssignableFrom(type)) {
			return super.getEntryType(type);
		}

		return this.findEntryTypeInInterfaces(type);
	}

	@SuppressWarnings("unchecked")
	private <T extends DataBaseEntry> Class<T> findEntryTypeInInterfaces(final Class<?> clazz) {
		if (DataBaseEntry.class.isAssignableFrom(clazz)) {
			return (Class<T>) clazz;
		}

		for (final Type iface : clazz.getGenericInterfaces()) {
			if (iface instanceof ParameterizedType) {
				final ParameterizedType pt = (ParameterizedType) iface;
				final Type rawType = pt.getRawType();

				if (rawType instanceof Class<?>) {
					final Class<?> rawClass = (Class<?>) rawType;

					if (SQLQueryable.class.isAssignableFrom(rawClass)) {
						final Type typeArg = pt.getActualTypeArguments()[0];
						if (typeArg instanceof Class<?>) {
							return (Class<T>) typeArg;
						}
					}

					final Class<T> result = this.findEntryTypeInInterfaces(rawClass);
					if (result != null) {
						return result;
					}
				}
			} else if (iface instanceof Class<?>) {
				final Class<T> result = this.findEntryTypeInInterfaces((Class<?>) iface);
				if (result != null) {
					return result;
				}
			}
		}

		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			return this.findEntryTypeInInterfaces(superclass);
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + clazz);
	}

	public <T extends DataBaseEntry> Class<? extends SQLQueryable<? extends DataBaseEntry>>[] resolveDependencies(
			final Class<? extends SQLQueryable<T>> queryableType) {
		Objects.requireNonNull(queryableType);

		if (AbstractDBView.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBView<T>> viewType = (Class<? extends AbstractDBView<T>>) queryableType;
			final Class<T> entryType = this.getEntryType(viewType);

			final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] viewDep = this
					.resolveViewDependencies(viewType);
			final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] entryDep = this
					.resolveEntryDependencies(entryType);

			return PCUtils.combineArrays(viewDep, entryDep);
		} else if (AbstractDBTable.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBTable<T>> tableType = (Class<? extends AbstractDBTable<T>>) queryableType;
			final Class<T> entryType = this.getEntryType(tableType);

			final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] entryDep = this
					.resolveEntryDependencies(entryType);

			return entryDep;
		}

		throw new IllegalArgumentException("Unknown class type: " + queryableType.getName());
	}

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<? extends DataBaseEntry>>[] resolveViewDependencies(
			final Class<? extends AbstractDBView<T>> viewType) {
		if (!viewType.isAnnotationPresent(DB_View.class))
			return new Class[0];

		final DB_View dbView = viewType.getAnnotation(DB_View.class);

		final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] baseClasses = Arrays.stream(dbView.tables())
				.filter(t -> !t.join().equals(ViewTable.Type.MAIN_UNION_ALL)
						&& !t.join().equals(ViewTable.Type.MAIN_UNION) && !t.typeName().equals(Class.class))
				.map(t -> t.typeName()).collect(Collectors.toList()).toArray(new Class[0]);

		final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] unionClasses = Arrays
				.stream(dbView.unionTables()).filter(t -> !t.typeName().equals(Class.class)).map(t -> t.typeName())
				.collect(Collectors.toList()).toArray(new Class[0]);

		return PCUtils.combineArrays(baseClasses, unionClasses);
	}

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<? extends DataBaseEntry>>[] resolveEntryDependencies(
			final Class<T> entryType) {
		final List<Class<? extends SQLQueryable<? extends DataBaseEntry>>> deps = new ArrayList<>();

		for (final Field f : super.sortFields(entryType.getDeclaredFields())) {
			if (!f.isAnnotationPresent(Column.class) || !f.isAnnotationPresent(ForeignKey.class))
				continue;

			final ForeignKey fk = f.getAnnotation(ForeignKey.class);
			deps.add((Class<? extends SQLQueryable<?>>) fk.table());
		}

		return deps.toArray(new Class[0]);
	}

}
