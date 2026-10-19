package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;

@Documented
@Retention(RUNTIME)
@Target({ TYPE_USE })
public @interface Cached {

	@TypeHint(type = DefaultQueryHints.CACHED)
	boolean value() default true;

}
