package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.table.DefaultQueryHints;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
@QueryHint(type = DefaultQueryHints.PARAM_OR_IS_NULL, value = "true")
public @interface OrIsNull {}
