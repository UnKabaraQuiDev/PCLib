package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.view.AbstractDBView;

public class SpringDataBaseEntryUtils extends BaseProxyDataBaseEntryUtils {

	public SpringDataBaseEntryUtils(
			final ColumnTypeRegistry typeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		super(typeRegistry, protocol, structureVisitor, functionResolver);
	}

	public SpringDataBaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		super(typeRegistry, protocolName);
	}

	public SpringDataBaseEntryUtils(final String protocol) {
		super(protocol);
	}

	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> type) {
		if (SQLQueryable.class.isAssignableFrom(type)) {
			return super.getEntryType(type);
		}

		return this.findEntryTypeInInterfaces(type);
	}

	public <T extends DataBaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveDependencies(final Class<? extends SQLQueryable<T>> queryableType) {
		Objects.requireNonNull(queryableType);

		if (AbstractDBView.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBView<T>> viewType = (Class<? extends AbstractDBView<T>>) queryableType;
			final Class<T> entryType = this.getEntryType(viewType);

			final Class<? extends SQLQueryable<?>>[] viewDep = this.resolveViewDependencies(viewType);
			final Class<? extends SQLQueryable<?>>[] entryDep = this.resolveEntryDependencies(entryType);

			return PCUtils.combineArrays(viewDep, entryDep);
		} else if (AbstractDBTable.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBTable<T>> tableType = (Class<? extends AbstractDBTable<T>>) queryableType;
			final Class<T> entryType = this.getEntryType(tableType);

			return this.resolveEntryDependencies(entryType);
		}

		throw new IllegalArgumentException("Unknown class type: " + queryableType.getName());
	}

	@SuppressWarnings("unchecked")
	private <T extends DataBaseEntry> Class<T> findEntryTypeInInterfaces(final Class<?> clazz) {
		if (DataBaseEntry.class.isAssignableFrom(clazz)) {
			return (Class<T>) clazz;
		}

		for (final Type iface : clazz.getGenericInterfaces()) {
			if (iface instanceof final ParameterizedType pt) {
				final Type rawType = pt.getRawType();

				if (rawType instanceof final Class<?> rawClass) {
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

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<?>>[] resolveEntryDependencies(final Class<T> entryType) {
		final List<Class<? extends SQLQueryable<?>>> deps = new ArrayList<>();

		for (final Field f : super.sortFields(entryType.getDeclaredFields())) {
			if (!f.isAnnotationPresent(Column.class) || !f.isAnnotationPresent(ForeignKey.class)) {
				continue;
			}

			final ForeignKey fk = f.getAnnotation(ForeignKey.class);
			deps.add(fk.table());
		}

		return deps.toArray(new Class[0]);
	}

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveViewDependencies(final Class<? extends AbstractDBView<T>> viewType) {
		if (!viewType.isAnnotationPresent(DB_View.class)) {
			return new Class[0];
		}

		final DB_View dbView = viewType.getAnnotation(DB_View.class);

		final Class<? extends SQLQueryable<?>>[] baseClasses = Arrays.stream(dbView.tables())
				.filter(t -> !ViewTable.Type.MAIN_UNION_ALL.equals(t.join()) && !ViewTable.Type.MAIN_UNION.equals(t.join())
						&& !t.typeName().equals(Class.class))
				.map(ViewTable::typeName)
				.collect(Collectors.toList())
				.toArray(new Class[0]);

		final Class<? extends SQLQueryable<?>>[] unionClasses = Arrays.stream(dbView.unionTables())
				.filter(t -> !t.typeName().equals(Class.class))
				.map(UnionTable::typeName)
				.collect(Collectors.toList())
				.toArray(new Class[0]);

		return PCUtils.combineArrays(baseClasses, unionClasses);
	}

}
