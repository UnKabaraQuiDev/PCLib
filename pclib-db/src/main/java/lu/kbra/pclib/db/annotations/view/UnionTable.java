package lu.kbra.pclib.db.annotations.view;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface UnionTable {

	ViewColumn[] columns() default {};

	String condition() default "";

	String name() default "";

	Class<?> typeName() default Class.class;

}
