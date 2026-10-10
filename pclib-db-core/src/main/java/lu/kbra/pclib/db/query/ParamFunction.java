package lu.kbra.pclib.db.query;

import java.util.function.Function;

public interface ParamFunction<D> extends Function<Object[], Object> {

	@Override
	D apply(Object[] t);

	default <T extends D> T get(Object... ts) {
		return (T) apply(ts);
	}

}
