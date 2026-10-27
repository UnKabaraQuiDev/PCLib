package lu.kbra.pclib.db.query.returns.cached;

import java.util.Iterator;
import java.util.List;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class CIteratorReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(final Class<?> returnType, final HintsOwner owner) {
		return returnType == Iterator.class && owner.getBooleanHint(DefaultQueryHints.CACHED, false) ? ReturnTypeMapper.SUPPORTED
				: ReturnTypeMapper.NOT_SUPPORTED;
	}

	@Override
	public Type getDefaultStrategy() {
		return Type.LIST_EMPTY;
	}

	@Override
	public Iterator<?> apply(final Type type, final Object value) {
		final List<?> list = (List<?>) value;
		return list.iterator();
	}

}
