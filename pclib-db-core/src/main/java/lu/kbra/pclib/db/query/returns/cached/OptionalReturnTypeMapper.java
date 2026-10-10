package lu.kbra.pclib.db.query.returns.cached;

import java.util.Optional;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class OptionalReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(Class<?> returnType, HintsOwner owner) {
		return returnType == Optional.class ? SUPPORTED : NOT_SUPPORTED;
	}

	@Override
	public Object apply(final Query.Type type, final Object value) {
		return type.isNullable() ? Optional.ofNullable(value) : Optional.of(value);
	}

}
