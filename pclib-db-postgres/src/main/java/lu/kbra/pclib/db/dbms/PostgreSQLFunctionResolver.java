package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.domain.dialect.StrictMapSQLFunctionResolver;

public class PostgreSQLFunctionResolver extends StrictMapSQLFunctionResolver {

	public PostgreSQLFunctionResolver() {
		// Aggregate
		this.put("count", "count");
		this.put("max", "max");
		this.put("min", "min");
		this.put("avg", "avg");
		this.put("sum", "sum");

		// Math
		this.put("abs", "abs");
		this.put("ceil", "ceil");
		this.put("floor", "floor");
		this.put("round", "round");
		this.put("sqrt", "sqrt");
		this.put("pow", "power");
		this.put("mod", "mod");
		this.put("rand", "random");

		// String
		this.put("length", "length");
		this.put("lower", "lower");
		this.put("upper", "upper");
		this.put("trim", "trim");
		this.put("concat", "concat");
		this.put("substring", "substring");
		this.put("replace", "replace");
		this.put("locate", "position");

		// Date/Time
		this.put("now", "now");
		this.put("current_date", "current_date");
		this.put("current_time", "current_time");
		this.put("current_timestamp", "current_timestamp");
		this.put("year", "extract");
		this.put("month", "extract");
		this.put("day", "extract");
		this.put("hour", "extract");
		this.put("minute", "extract");
		this.put("second", "extract");

		// Misc
		this.put("coalesce", "coalesce");
		this.put("nullif", "nullif");

		// Requires extension pgcrypto
		this.put("uuid", "gen_random_uuid");
	}

}
