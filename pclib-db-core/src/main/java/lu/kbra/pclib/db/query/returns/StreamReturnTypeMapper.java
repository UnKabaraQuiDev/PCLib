package lu.kbra.pclib.db.query.returns;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lu.kbra.pclib.db.annotations.query.Query.Type;

public class StreamReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(Class<?> returnType) {
		return returnType == Stream.class;
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
