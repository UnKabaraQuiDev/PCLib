package lu.kbra.pclib.db.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.view.AbstractDBView;

import lombok.Data;

public interface PostgreSQLView<T extends DatabaseEntry> extends AbstractDBView<T> {

	@Data
	static final class PostgreSQLRefreshViewCommand<T extends DatabaseEntry> implements SQLCommand<T, Void> {

		private final boolean concurrently;
		private final boolean withData;

		@Override
		public Void execute(final AbstractConnection c, final SQLQueryable<T> instance) throws SQLException, DBException {
			if (!instance.getDatabaseEntryUtils().getDbmsQualifierName().equals(PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME)) {
				return null;
			}

			final String sql = "REFRESH MATERIALIZED VIEW " + (this.concurrently ? "CONCURRENTLY" : "") + " " + instance.getQualifiedName()
					+ " " + (this.withData ? "WITH DATA" : "WITH NO DATA") + ";";

			final SQLQueryableHookManager queryableHookManager = instance.getQueryableHookManager();

			try (Statement stmt = c.createStatement()) {
				stmt.execute(sql);
			} catch (final SQLException e) {
				final List<Throwable> suppressed = queryableHookManager.executeError(RuleHookType.ERROR_EXECUTE, instance, c, e, null);
				throw new InternalDBException("Error executing query.", sql, instance.getStructure(), e).addSuppressed(suppressed);
			}

			return null;
		}

	}

	default void updateView(final boolean concurrently, final boolean withData) {
		this.execute(new PostgreSQLRefreshViewCommand<>(concurrently, withData));
	}

	default void updateView() {
		updateView(false, true);
	}

}
