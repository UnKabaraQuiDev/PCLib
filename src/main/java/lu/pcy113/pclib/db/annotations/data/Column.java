package lu.pcy113.pclib.db.annotations.data;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {

	/**
	 * The name of the column in the database.
	 * 
	 * @return The name of the column.
	 */
	String name();

	/**
	 * The type of the column in the database.
	 * 
	 * @return The type of the column.
	 */
	String type() default "AUTO";

	/**
	 * Whether the column is a primary key.
	 * 
	 * @return True if the column is a primary key, false otherwise.
	 */
	boolean primaryKey() default false;

	/**
	 * Whether the column is unique.
	 * 
	 * @return True if the column is unique, false otherwise.
	 */
	boolean unique() default false;

	/**
	 * Whether the column is not null.
	 * 
	 * @return True if the column is not null, false otherwise.
	 */
	boolean notNull() default false;

	/**
	 * The default value of the column.
	 * 
	 * @return The default value of the column.
	 */
	String defaultValue() default "";

}
