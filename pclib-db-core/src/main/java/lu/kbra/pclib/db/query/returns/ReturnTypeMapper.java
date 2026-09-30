package lu.kbra.pclib.db.query.returns;

import java.util.function.BiFunction;

import lu.kbra.pclib.db.annotations.query.Query;

public interface ReturnTypeMapper extends BiFunction<Query.Type, Object, Object> {

	boolean supportsReturnType(Class<?> returnType);

	default int getTypeParameterIndex() {
		return 0;
	}

	default Query.Type getDefaultStrategy() {
		return isRequireEnumeration() ? Query.Type.LIST_EMPTY : Query.Type.FIRST_NULL;
	}

	default boolean isRequireEnumeration() {
		return false;
	}

	@Override
	Object apply(Query.Type type, Object value);

}
