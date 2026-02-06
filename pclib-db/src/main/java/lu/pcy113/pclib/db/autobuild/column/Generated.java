package lu.pcy113.pclib.db.autobuild.column;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Generated {

	public static enum Type {

		VIRTUAL,
		STORED;

	}

	Type value() default Type.VIRTUAL;

}
