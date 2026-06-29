package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.domain.dialect.StrictMapSQLFunctionResolver;

public class MySQLFunctionResolver extends StrictMapSQLFunctionResolver {

	public MySQLFunctionResolver() {
		// Aggregate
		this.put("count", "COUNT");
		this.put("max", "MAX");
		this.put("min", "MIN");
		this.put("avg", "AVG");
		this.put("sum", "SUM");

		// Math
		this.put("abs", "ABS");
		this.put("ceil", "CEIL");
		this.put("floor", "FLOOR");
		this.put("round", "ROUND");
		this.put("sqrt", "SQRT");
		this.put("pow", "POW");
		this.put("mod", "MOD");
		this.put("rand", "RAND");

		// String
		this.put("length", "LENGTH");
		this.put("lower", "LOWER");
		this.put("upper", "UPPER");
		this.put("trim", "TRIM");
		this.put("concat", "CONCAT");
		this.put("substring", "SUBSTRING");
		this.put("replace", "REPLACE");
		this.put("locate", "LOCATE");

		// Date/Time
		this.put("now", "NOW");
		this.put("current_date", "CURRENT_DATE");
		this.put("current_time", "CURRENT_TIME");
		this.put("current_timestamp", "CURRENT_TIMESTAMP");
		this.put("year", "YEAR");
		this.put("month", "MONTH");
		this.put("day", "DAY");
		this.put("hour", "HOUR");
		this.put("minute", "MINUTE");
		this.put("second", "SECOND");

		// Misc
		this.put("coalesce", "COALESCE");
		this.put("nullif", "NULLIF");
		this.put("ifnull", "IFNULL");
		this.put("uuid", "UUID");
	}

}
