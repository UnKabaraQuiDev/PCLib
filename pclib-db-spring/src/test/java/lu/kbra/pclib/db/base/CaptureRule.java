package lu.kbra.pclib.db.base;

import java.sql.Statement;
import java.util.Arrays;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class CaptureRule implements BeforeRule {

	private static final int LONGEST = Arrays.stream(RuleHookType.values())
			.mapToInt(hookType -> hookType.name().substring(hookType.name().indexOf("_") + 1).length())
			.max()
			.orElse(0);

	private String latest;

	@Override
	public void executeBefore(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement pstmt,
			final Object data) {
		this.latest = queryable.getStatementAsSQL(pstmt);
	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return hookType.isBefore();
	}

}
