package shared;

import java.sql.Statement;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;

public class PrintRule implements BeforeRule {

	@Override
	public void executeBefore(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement pstmt,
			final Object data) {
		System.err.println(hookType + " | " + queryable.getStatementAsSQL(pstmt));
	}

}
