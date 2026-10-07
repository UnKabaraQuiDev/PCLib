package lu.kbra.pclib.db.autobuild.sqlite.meta;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface ForeignKeys {

	@QueryableHint(type = SQLiteDatabaseHints.FOREIGN_KEYS)
	boolean value();

	@DbmsFilter
	String dbms() default SQLiteDbmsProvider.DBMS_QUALIFIER_NAME;

}
