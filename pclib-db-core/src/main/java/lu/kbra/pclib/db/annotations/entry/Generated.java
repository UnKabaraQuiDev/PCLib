package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Generated {

	public static enum Type {

		VIRTUAL,
		STORED;

	}

	@ColumnHint(type = DefaultColumnHints.GENERATED_STORAGE_TYPE)
	Type value() default Type.VIRTUAL;

}
