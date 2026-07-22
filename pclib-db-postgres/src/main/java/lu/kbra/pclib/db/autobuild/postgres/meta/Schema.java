package lu.kbra.pclib.db.autobuild.postgres.meta;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Schema {

	@QueryableHint(type = PostgreSQLTableHints.LC_COLLATE)
	String collate();

	@QueryableHint(type = PostgreSQLTableHints.LC_CTYPE)
	String ctype();

	@DbmsFilter
	String dbms() default DatabaseEntryUtils.DBMS_FILTER_ALL;

}
