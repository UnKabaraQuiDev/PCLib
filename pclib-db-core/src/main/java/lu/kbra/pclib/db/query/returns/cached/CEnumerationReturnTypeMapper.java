package lu.kbra.pclib.db.query.returns.cached;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class CEnumerationReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(Class<?> returnType, HintsOwner owner) {
		return returnType == Enumeration.class && owner.getBooleanHint(DefaultQueryHints.CACHED, false) ? SUPPORTED : NOT_SUPPORTED;
	}

	@Override
	public Type getDefaultStrategy() {
		return Type.LIST_EMPTY;
	}

	@Override
	public Enumeration<?> apply(Type type, Object value) {
		final List<?> list = (List<?>) value;
		return new Enumeration<Object>() {

			final Iterator<?> iterator = list.iterator();

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public Object nextElement() {
				return iterator.next();
			}

		};

	}

}
