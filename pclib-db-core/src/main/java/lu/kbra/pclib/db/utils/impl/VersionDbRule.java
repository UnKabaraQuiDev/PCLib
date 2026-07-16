package lu.kbra.pclib.db.utils.impl;

import java.sql.ResultSet;
import java.util.Optional;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class VersionDbRule implements DatabaseEntryRule.UpdateRule, DatabaseEntryRule.DuringRule {

	@Override
	public <T extends DatabaseEntry> void during(
			final RuleHookType hookType,
			final T data,
			final SQLQueryable<? extends T> queryable,
			final Optional<ResultSet> result,
			final Optional<ResultSet> generatedKeys) {

	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return DatabaseEntryRule.UpdateRule.super.shouldRun(hookType, queryable)
				&& DatabaseEntryRule.DuringRule.super.shouldRun(hookType, queryable)
				&& AbstractDBTable.class.isInstance(queryable.getTargetClass());
	}

}
