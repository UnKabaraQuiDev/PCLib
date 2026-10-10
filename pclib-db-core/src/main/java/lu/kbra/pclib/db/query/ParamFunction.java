package lu.kbra.pclib.db.query;

import java.util.function.Function;

public interface ParamFunction extends Function<Object[], Object> {

	@Override
	Object apply(Object[] t);

	default <T> T get(Object... ts) {
		return (T) apply(ts);
	}

}
