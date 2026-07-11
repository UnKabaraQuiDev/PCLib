package lu.kbra.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.SQLQueryable;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
//@QueryableHint(type = DefaultQueryableHints.VIEW_TABLE, grouped = true, repeatable = true)
public @interface ViewTable {

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
	String asName() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_COLUMNS)
	ViewColumn[] columns() default {};

	@QueryableHint(type = DefaultQueryableHints.VIEW_DISTINCT)
	boolean distinct() default false;

	@QueryableHint(type = DefaultQueryableHints.VIEW_JOIN_TYPE)
	Type join() default Type.MAIN;

	@QueryableHint(type = DefaultQueryableHints.VIEW_NAME)
	String name() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_JOIN_ON_CONDITION)
	String on() default "";

	@QueryableHint(type = DefaultQueryableHints.VIEW_TYPE)
	Class<? extends SQLQueryable<?>> typeName();

}
