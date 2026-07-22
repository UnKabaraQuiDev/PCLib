package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.domain.dialect.StrictMapSQLFunctionResolver;

public class SQLiteFunctionResolver extends StrictMapSQLFunctionResolver {

	public SQLiteFunctionResolver() {
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
		this.put("pow", "pow");
		this.put("mod", "mod");
		this.put("rand", "random");

		// String
		this.put("length", "length");
		this.put("lower", "lower");
		this.put("upper", "upper");
		this.put("trim", "trim");
		this.put("concat", "concat");
		this.put("substring", "substr");
		this.put("replace", "replace");
		this.put("locate", "instr");

		// Date/Time
		this.put("now", "strftime('%Y-%m-%dT%H:%M:%fZ','now')");
		this.put("current_date", "strftime('%Y-%m-%d','now')");
		this.put("current_time", "strftime('%H:%M:%fZ','now')");
		this.put("current_timestamp", "strftime('%Y-%m-%dT%H:%M:%fZ','now')");

		// Misc
		this.put("coalesce", "coalesce");
		this.put("nullif", "nullif");

		// UUID must be emulated
		this.put("uuid", "lower(hex(randomblob(16)))");
	}

}
