package lu.kbra.pclib.db.annotations.entry.def;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.Version;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Version(default_ = "{F:current_timestamp}", expr = "{F:current_timestamp}")
public @interface TimeVersion {

}
