package lu.kbra.pclib.db.rule;

import java.sql.Statement;
import java.util.Arrays;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceRule implements BeforeRule {

	private static final int LONGEST = Arrays.stream(RuleHookType.values())
			.mapToInt(hookType -> hookType.name().substring(hookType.name().indexOf("_") + 1).length())
			.max()
			.orElse(0);

	@Override
	public void executeBefore(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement pstmt,
			final Object data) {
		TraceRule.log.trace(PCUtils.rightPadString(hookType.name().substring(hookType.name().indexOf("_") + 1), " ", LONGEST) + " | "
				+ PCUtils.getStatementAsSQL(pstmt));
	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return hookType.isBefore();
	}

}
