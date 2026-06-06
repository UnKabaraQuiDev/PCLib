package lu.kbra.pclib.db.autobuild.column.type.meta;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, TYPE_USE })
@Repeatable(TypeHints.class)
public @interface TypeHint {

	String value() default "";

	String type();

}
