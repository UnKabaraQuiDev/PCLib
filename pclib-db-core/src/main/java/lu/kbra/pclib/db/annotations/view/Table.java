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
import lu.kbra.pclib.db.impl.SQLQueryable;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

	public static enum Type {
		MAIN,
		MAIN_UNION,
		MAIN_UNION_ALL,
		LEFT,
		RIGHT,
		INNER,
		FULL,
		CROSS;
	}

	@QueryableHint(type = DefaultQueryableHints.VIEW_AS_NAME)
	@QueryHint(type = DefaultQueryHints.AS_NAME)
	String asName() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMNS)
	@QueryHint(type = DefaultQueryHints.COLUMNS)
	SelectColumn[] columns() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_DISTINCT)
	@QueryHint(type = DefaultQueryHints.DISTINCT)
	boolean distinct() default false;

	@QueryableHint(type = DefaultQueryableHints.VIEW_JOIN_TYPE)
	@QueryHint(type = DefaultQueryHints.JOIN_TYPE)
	Type join() default Type.MAIN;

	@QueryableHint(type = DefaultQueryableHints.VIEW_NAME)
	@QueryHint(type = DefaultQueryHints.NAME)
	String name() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_JOIN_ON_CONDITION)
	@QueryHint(type = DefaultQueryHints.ON_CONDITION)
	String on() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_TYPE)
	@QueryHint(type = DefaultQueryHints.TYPE)
	Class<? extends SQLQueryable<?>> typeName();

}
