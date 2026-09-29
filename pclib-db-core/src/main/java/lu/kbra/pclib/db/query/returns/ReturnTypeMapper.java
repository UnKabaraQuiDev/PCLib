package lu.kbra.pclib.db.query.returns;

import java.util.function.BiFunction;

import lu.kbra.pclib.db.annotations.query.Query;

public interface ReturnTypeMapper extends BiFunction<Query.Type, Object, Object> {

	boolean supportsReturnType(Class<?> returnType);

	boolean isRequireEnumeration();

	@Override
	Object apply(Query.Type type, Object value);

}
