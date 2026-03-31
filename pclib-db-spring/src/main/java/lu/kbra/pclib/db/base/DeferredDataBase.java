package lu.kbra.pclib.db.base;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.MethodParameter;

import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.DelegatingConnection;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredDBTransaction;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.intercept.TransactionQueryMethodInterceptor;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.table.NTDataBaseTable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredDataBase extends DataBase {

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	public DeferredDataBase(final DataBaseConnector connector, final String name, final DataBaseEntryUtils dbEntryUtils) {
		super(connector, name, dbEntryUtils);
	}

	public DeferredDataBase(
			final DataBaseConnector connector,
			final String name,
			final String charSet,
			final String collation,
			final DataBaseEntryUtils dbEntryUtils) {
		super(connector, name, charSet, collation, dbEntryUtils);
	}

	public DeferredDataBase(final DataBaseConnector connector, final String name, final String charSet, final String collation) {
		super(connector, name, charSet, collation);
	}

	public DeferredDataBase(final DataBaseConnector connector, final String name) {
		super(connector, name);
	}

	public DeferredDataBase(final DataBaseConnectorFactory connector, final String name, final DataBaseEntryUtils dbEntryUtils) {
		super(connector, name, dbEntryUtils);
	}

	public DeferredDataBase(
			final DataBaseConnectorFactory connector,
			final String name,
			final String charSet,
			final String collation,
			final DataBaseEntryUtils dbEntryUtils) {
		super(connector, name, charSet, collation, dbEntryUtils);
	}

	public DeferredDataBase(final DataBaseConnectorFactory connector, final String name, final String charSet, final String collation) {
		super(connector, name, charSet, collation);
	}

	public DeferredDataBase(final DataBaseConnectorFactory connector, final String name) {
		super(connector, name);
	}

	@Override
	public DeferredDBTransaction createTransaction() {
		return new DeferredAbstractTableTransaction();
	}

	public class DeferredAbstractTableTransaction extends AbstractTableTransaction implements DeferredDBTransaction {

		protected final QueryMethodInterceptor interceptor;
		protected final Map<Class<?>, SQLQueryable<?>> cache = new HashMap<>();

		public DeferredAbstractTableTransaction() {
			this.interceptor = new TransactionQueryMethodInterceptor(() -> {
				this.lock.lock();
				return new DelegatingConnection(this.connection, c -> this.lock.unlock());
			});
		}

		@Override
		public <X extends DataBaseEntry, V extends DeferredDataBaseTable<X>> V use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!DeferredDataBase.this.equals(inst.getDataBase())) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return this.createProxy((Class<V>) inst.getTableClass());
		}

		@Override
		public <X extends DataBaseEntry, V extends DeferredNTDataBaseTable<X>> V use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!DeferredDataBase.this.equals(inst.getDataBase())) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return this.createProxy((Class<V>) inst.getTableClass());
		}

		@Override
		public <X extends DataBaseEntry, V extends DataBaseTable<X>> V use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!DeferredDataBase.this.equals(inst.getDataBase())) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return this.createProxy((Class<V>) inst.getTableClass());
		}

		@Override
		public <X extends DataBaseEntry, V extends NTDataBaseTable<X>> V use(final V inst) {
			Objects.requireNonNull(inst, "Table instance cannot be null.");
			if (!DeferredDataBase.this.equals(inst.getDataBase())) {
				throw new IllegalArgumentException("The table should be in the same database as the transaction.");
			}
			return this.createProxy((Class<V>) inst.getTableClass());
		}

		public <X extends DataBaseEntry, V extends AbstractDBTable<X>> V createProxy(final Class<V> repositoryClass) {
			if (this.cache.containsKey(repositoryClass)) {
				return (V) this.cache.get(repositoryClass);
			}

			final Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(repositoryClass);
			enhancer.setCallback(this.interceptor);

			final V dbProxy;

			final Constructor<?> ctor = repositoryClass.getDeclaredConstructors()[0];
			if (ctor == null || ctor.getParameterCount() == 0) {
				throw new UnsupportedOperationException(repositoryClass + " doesn't define a constructor.");
			} else {
				final Parameter[] params = ctor.getParameters();
				final Object[] args = new Object[params.length];

				for (int i = 0; i < params.length; i++) {
					final Parameter p = params[i];
					final Type genericType = p.getParameterizedType();

					if (genericType instanceof final ParameterizedType pt) {
						final Type arg = pt.getActualTypeArguments()[0];
						if (arg instanceof final Class<?> clazz && SQLQueryable.class.isAssignableFrom(clazz)) {
							args[i] = repositoryClass;
							continue;
						}
					}

					final DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(ctor, i), true);
					final Qualifier qual = p.getAnnotation(Qualifier.class);
					final String name = qual != null ? qual.value() : null;
					args[i] = DeferredDataBase.this.beanFactory.resolveDependency(desc, name);
				}

				dbProxy = (V) enhancer.create(Arrays.stream(params).map(Parameter::getType).toArray(Class<?>[]::new), args);
			}

			if (DeferredDataBaseTable.class.isAssignableFrom(repositoryClass)) {
				((DeferredDataBaseTable) dbProxy).init(repositoryClass);
			} else if (DeferredNTDataBaseTable.class.isAssignableFrom(repositoryClass)) {
				((DeferredNTDataBaseTable) dbProxy).init(repositoryClass);
			}

			this.interceptor.registerDelegate(dbProxy, repositoryClass);

			DeferredDataBase.this.beanFactory.autowireBean(dbProxy);
			DeferredDataBase.this.beanFactory.initializeBean(dbProxy, Introspector.decapitalize(repositoryClass.getSimpleName()));

			this.cache.put(repositoryClass, dbProxy);

			return dbProxy;
		}

	}

}
