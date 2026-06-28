package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.domain.dialect.AbstractSQLFunctionResolver;

public class SQLiteFunctionResolver extends AbstractSQLFunctionResolver {

	public SQLiteFunctionResolver() {
		this.put("count", "count");
		this.put("max", "max");
		this.put("min", "min");
		this.put("avg", "avg");
		this.put("sum", "sum");
	}

}
