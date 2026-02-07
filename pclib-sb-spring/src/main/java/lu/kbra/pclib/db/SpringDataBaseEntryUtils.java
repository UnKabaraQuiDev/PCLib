package lu.kbra.pclib.db;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.type.ListType;
import lu.kbra.pclib.db.utils.BaseProxyDataBaseEntryUtils;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DeferredDataBaseView;

@Component
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SpringDataBaseEntryUtils extends BaseProxyDataBaseEntryUtils {

	public SpringDataBaseEntryUtils() {
		appendSpringTypes();
	}

	private void appendSpringTypes() {
		// java types -----
//		typeMap.put(CircularFifoQueue.class, col -> new CircularFifoQueueType(col.length()));
		typeMap.put(List.class, col -> new ListType());
		typeMap.put(ArrayList.class, col -> new ListType());
		typeMap.put(LinkedList.class, col -> new ListType());

		// native types -----
//		typeMap.put(CircularFifoQueueType.class, col -> new CircularFifoQueueType(col.length()));
		typeMap.put(ListType.class, col -> new ListType());
	}

	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(
			Class<? extends SQLQueryable<? extends DataBaseEntry>> type) {
		if (DeferredDataBaseTable.class.isAssignableFrom(type) || DeferredDataBaseView.class.isAssignableFrom(type)) {
			return super.getEntryType(type);
		}

		return findEntryTypeInInterfaces(type);
	}

	@SuppressWarnings("unchecked")
	private <T extends DataBaseEntry> Class<T> findEntryTypeInInterfaces(Class<?> clazz) {
		if (DataBaseEntry.class.isAssignableFrom(clazz)) {
			return (Class<T>) clazz;
		}

		for (Type iface : clazz.getGenericInterfaces()) {
			if (iface instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) iface;
				Type rawType = pt.getRawType();

				if (rawType instanceof Class<?>) {
					final Class<?> rawClass = (Class<?>) rawType;

					if (SQLQueryable.class.isAssignableFrom(rawClass)) {
						Type typeArg = pt.getActualTypeArguments()[0];
						if (typeArg instanceof Class<?>) {
							return (Class<T>) typeArg;
						}
					}

					Class<T> result = findEntryTypeInInterfaces(rawClass);
					if (result != null) {
						return result;
					}
				}
			} else if (iface instanceof Class<?>) {
				Class<T> result = findEntryTypeInInterfaces((Class<?>) iface);
				if (result != null) {
					return result;
				}
			}
		}

		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			return findEntryTypeInInterfaces(superclass);
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + clazz);
	}

	public <T extends DataBaseEntry> Class<? extends SQLQueryable<? extends DataBaseEntry>>[] resolveDependencies(
			Class<? extends SQLQueryable<T>> queryableType) {
		Objects.requireNonNull(queryableType);

		if (AbstractDBView.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBView<T>> viewType = (Class<? extends AbstractDBView<T>>) queryableType;
			final Class<T> entryType = getEntryType(viewType);

			final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] viewDep = resolveViewDependencies(viewType);
			final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] entryDep = resolveEntryDependencies(
					entryType);

			return PCUtils.combineArrays(viewDep, entryDep);
		} else if (AbstractDBTable.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBTable<T>> tableType = (Class<? extends AbstractDBTable<T>>) queryableType;
			final Class<T> entryType = getEntryType(tableType);

			final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] entryDep = resolveEntryDependencies(
					entryType);

			return entryDep;
		}

		throw new IllegalArgumentException("Unknown class type: " + queryableType.getName());
	}

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<? extends DataBaseEntry>>[] resolveViewDependencies(
			Class<? extends AbstractDBView<T>> viewType) {
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
			Class<T> entryType) {
		final List<Class<? extends SQLQueryable<? extends DataBaseEntry>>> deps = new ArrayList<>();

		for (Field f : super.sortFields(entryType.getDeclaredFields())) {
			if (!f.isAnnotationPresent(Column.class))
				continue;
			if (!f.isAnnotationPresent(ForeignKey.class))
				continue;

			final ForeignKey fk = f.getAnnotation(ForeignKey.class);
			deps.add((Class<SQLQueryable<?>>) fk.table());
		}

		return deps.toArray(new Class[0]);
	}

}
