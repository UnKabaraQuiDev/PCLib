package lu.kbra.pclib.db.autobuild.column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(Checks.class)
public @interface Check {

	public static final String FIELD_NAME_PLACEHOLDER = "%NAME%";

	String value();

}
