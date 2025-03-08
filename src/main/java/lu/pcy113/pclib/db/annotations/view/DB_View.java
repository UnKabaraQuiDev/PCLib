package lu.pcy113.pclib.db.annotations.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DB_View {

	String name();
	
	ViewTable[] tables();
	
	String condition() default "";
	
	String groupBy() default "";

	OrderBy[] orderBy() default {};

}
