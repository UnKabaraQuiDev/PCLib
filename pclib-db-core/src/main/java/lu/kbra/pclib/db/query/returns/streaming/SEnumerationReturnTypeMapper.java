package lu.kbra.pclib.db.query.returns.streaming;

import java.util.Enumeration;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class SEnumerationReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(Class<?> returnType, HintsOwner owner) {
		return returnType == Enumeration.class && !owner.getBooleanHint(DefaultQueryHints.CACHED, false) ? SUPPORTED : NOT_SUPPORTED;
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
