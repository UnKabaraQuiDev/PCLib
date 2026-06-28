package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.domain.dialect.AbstractSQLFunctionResolver;

public class MySQLFunctionResolver extends AbstractSQLFunctionResolver {

	public MySQLFunctionResolver() {
		this.put("count", "COUNT");
		this.put("max", "MAX");
		this.put("min", "MIN");
		this.put("avg", "AVG");
		this.put("sum", "SUM");
	}

}
