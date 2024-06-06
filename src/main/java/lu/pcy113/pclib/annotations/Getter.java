package lu.pcy113.pclib.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(FIELD)
public @interface Getter {
	String name() default "get{capilatizedName}";
	Class<?> returnType() default Void.class;
}
