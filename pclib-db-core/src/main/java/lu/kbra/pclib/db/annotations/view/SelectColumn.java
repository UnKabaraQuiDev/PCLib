package lu.kbra.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.query.QueryHint;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SelectColumn {

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMN_NAME)
	@QueryHint(type = DefaultQueryHints.JOIN_COLUMN_NAME)
	String name() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMN_FUNCTION)
	@QueryHint(type = DefaultQueryHints.JOIN_COLUMN_FUNCTION)
	String func() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMN_AS_NAME)
	@QueryHint(type = DefaultQueryHints.JOIN_COLUMN_AS_NAME)
	String asName() default "";
}
