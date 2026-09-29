package lu.kbra.pclib.db.query.returns;

import java.util.Enumeration;
import java.util.Iterator;

import lu.kbra.pclib.db.annotations.query.Query.Type;

public class IteratorReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(Class<?> returnType) {
		return returnType == Iterator.class;
	}

	@Override
	public boolean isRequireEnumeration() {
		return true;
	}

	@Override
	public Iterator<?> apply(Type type, Object value) {
		return new Iterator<Object>() {

			final Enumeration<?> enumeration = (Enumeration<?>) value;

			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public Object next() {
				return enumeration.nextElement();
			}

		};
	}

}
