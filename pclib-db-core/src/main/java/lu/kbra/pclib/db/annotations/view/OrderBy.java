package lu.kbra.pclib.db.annotations.view;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

@Documented
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface OrderBy {

	public static enum Type {
		ASC,
		DESC;
	}

	@QueryableHint(type = DefaultQueryableHints.VIEW_ORDER_BY_COLUMN)
	String column();

	@QueryableHint(type = DefaultQueryableHints.VIEW_ORDER_BY_DIR)
	Type type() default Type.ASC;

}
