package lu.kbra.pclib.db.query.returns;

import java.util.Enumeration;

import lu.kbra.pclib.db.annotations.query.Query.Type;

public class EnumerationReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(Class<?> returnType) {
		return returnType == Enumeration.class;
	}

	@Override
	public boolean isRequireEnumeration() {
		return true;
	}

	@Override
	public Enumeration<?> apply(Type type, Object value) {
		return (Enumeration<?>) value;
	}

}
