package lu.kbra.pclib.db.query.returns.streaming;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class SStreamReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(Class<?> returnType, HintsOwner owner) {
		return returnType == Stream.class && !owner.getBooleanHint(DefaultQueryHints.CACHED, false) ? SUPPORTED : NOT_SUPPORTED;
	}

	@Override
	public boolean isRequireEnumeration() {
		return true;
	}

	@Override
	public Stream<?> apply(Type type, Object value) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Object>() {

			final Enumeration<?> enumeration = (Enumeration<?>) value;

			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public Object next() {
				return enumeration.nextElement();
			}

		}, Spliterator.ORDERED), false);
	}

}
