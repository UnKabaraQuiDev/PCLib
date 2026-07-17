package shared;
import java.sql.Statement;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.RuleHookType;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;

public class PrintDbRule implements BeforeRule {

	@Override
	public void executeBefore(RuleHookType hookType, SQLQueryable<?> queryable, Statement pstmt, Object data) {
		System.err.println(hookType + " | " + PCUtils.getStatementAsSQL(pstmt));
	}

	@Override
	public boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
		return hookType.isBefore();
	}

}
