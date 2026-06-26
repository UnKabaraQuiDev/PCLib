package lu.kbra.pclib.db.annotations.entry.def;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DefaultValue;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@DefaultValue(dbms = "postgresql", value = "gen_random_uuid()")
@DefaultValue(dbms = "mysql", value = "(UUID())")
@DefaultValue(
		dbms = "sqlite",
		value = "(lower(hex(randomblob(4))) || '-' || hex(randomblob(2)) || '-4' || substr(hex(randomblob(2)),2) || '-' || "
				+ "substr('89ab', abs(random()) % 4 + 1, 1) || substr(hex(randomblob(2)),2) || '-' || hex(randomblob(6)))"
)
public @interface RandomUuid {

}
