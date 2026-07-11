package lu.kbra.pclib.db.domain.dialect;

import lu.kbra.pclib.db.exception.FunctionNotFoundException;
import lu.kbra.pclib.impl.function.ThrowingFunction;

public interface SQLFunctionResolver extends ThrowingFunction<String, String, FunctionNotFoundException> {

	String applyOrDefault(String key, String default_);

}
