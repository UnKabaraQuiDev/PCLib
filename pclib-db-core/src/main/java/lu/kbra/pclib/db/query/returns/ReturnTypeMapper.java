package lu.kbra.pclib.db.query.returns;

import java.util.function.BiFunction;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.impl.HintsOwner;

public interface ReturnTypeMapper extends BiFunction<Query.Type, Object, Object> {

	Integer NOT_SUPPORTED = null;
	Integer SUPPORTED = 50;
	Integer FALL_BACK = 100;

	Integer supportsReturnType(Class<?> returnType, HintsOwner hints);

	default int getTypeParameterIndex() {
		return 0;
	}

	default Query.Type getDefaultStrategy() {
		return this.isRequireEnumeration() ? Query.Type.LIST_EMPTY : Query.Type.FIRST_NULL;
	}

	default boolean isRequireEnumeration() {
		return false;
	}

	@Override
	Object apply(Query.Type type, Object value);

}
