package lu.kbra.pclib.db.query.returns;

import java.util.Optional;

import lu.kbra.pclib.db.annotations.query.Query;

public final class OptionalReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(final Class<?> returnType) {
		return returnType == Optional.class;
	}

	@Override
	public boolean isRequireEnumeration() {
		return false;
	}

	@Override
	public Object apply(final Query.Type type, final Object value) {
		return type.isNullable() ? Optional.ofNullable(value) : Optional.of(value);
	}

}
