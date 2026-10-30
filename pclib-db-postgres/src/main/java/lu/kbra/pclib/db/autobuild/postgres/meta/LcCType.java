package lu.kbra.pclib.db.autobuild.postgres.meta;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface LcCType {

	@QueryableHint(type = PostgreSQLQueryableHints.LC_CTYPE)
	String ctype();

	@DbmsFilter
	String dbms() default PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME;

}
