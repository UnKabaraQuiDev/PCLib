package lu.kbra.pclib.db.query.returns.streaming;

import java.util.Enumeration;
import java.util.Iterator;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class SIteratorReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(final Class<?> returnType, final HintsOwner owner) {
		return returnType == Iterator.class && !owner.getBooleanHint(DefaultQueryHints.CACHED, false) ? ReturnTypeMapper.SUPPORTED
				: ReturnTypeMapper.NOT_SUPPORTED;
	}

	@Override
	public boolean isRequireEnumeration() {
		return true;
	}

	@Override
	public Iterator<?> apply(final Type type, final Object value) {
		return new Iterator<Object>() {

			final Enumeration<?> enumeration = (Enumeration<?>) value;

			@Override
			public boolean hasNext() {
				return this.enumeration.hasMoreElements();
			}

			@Override
			public Object next() {
				return this.enumeration.nextElement();
			}

		};
	}

}
