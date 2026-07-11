package lu.kbra.pclib.db.annotations.queryable.def;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface DefinedName {

	@QueryableHint(type = DefaultQueryableHints.DEFINED_NAME)
	String value();

}
