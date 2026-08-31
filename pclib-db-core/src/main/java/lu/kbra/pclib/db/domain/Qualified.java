package lu.kbra.pclib.db.domain;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(CLASS)
@Target(TYPE_USE)
/**
 * Indicates a {@link String} contains a qualified version of that name for the current DBMS.
 */
public @interface Qualified {

}
