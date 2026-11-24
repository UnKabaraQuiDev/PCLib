package lu.kbra.pclib.db.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLCommand;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Data;

@Data
public class TransformingQuerySQLCommand<T extends DatabaseEntry, R> implements SQLCommand<T, R> {

	private final TransformingQuery<T, R> query;

	@Override
	public R execute(final AbstractConnection c, final SQLQueryable<T> instance) throws SQLException, DBException {
		final SQLQueryableHookManager queryableHookManager = instance.getQueryableHookManager();
		final DatabaseEntryUtils databaseEntryUtils = instance.getDatabaseEntryUtils();

		String querySQL = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		try {
			// prepare query hook
			queryableHookManager.executePrepare(RuleHookType.PREPARE_QUERY, instance, c, this.query);

			pstmt = c.prepareStatement(this.query.getPreparedQuerySQL(instance));

			this.query.updateQuerySQL(instance, pstmt);
			querySQL = instance.getStatementAsSQL(pstmt);

			// before query hook
			queryableHookManager.executeBefore(RuleHookType.BEFORE_QUERY, instance, c, pstmt, this.query);
			result = pstmt.executeQuery();

			// during query hook
			queryableHookManager.executeDuring(RuleHookType.DURING_QUERY, instance, c, pstmt, this.query);
			final List<T> output = new ArrayList<>();
			databaseEntryUtils.fillLoadAll(instance, instance.getEntryClass(), result, output::add);
			final R r = query.transform(instance, output);

			// after query hook
			queryableHookManager.executeAfter(RuleHookType.AFTER_QUERY, instance, c, pstmt, query);

			if (!query.closeResultSet()) {
				result = null;
				pstmt = null;
			}
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = queryableHookManager.executeError(RuleHookType.ERROR_QUERY, instance, c, e, this.query);
			throw new InternalDBException("Error executing query.", querySQL, instance.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = queryableHookManager.executeError(RuleHookType.ERROR_QUERY, instance, c, e, this.query);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

}
