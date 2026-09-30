package lu.kbra.pclib.db.type.returns;

import java.util.Enumeration;
import java.util.Iterator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

import reactor.core.publisher.Flux;

@Component
@ConditionalOnClass(Flux.class)
public class FluxReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(Class<?> returnType) {
		return returnType == Flux.class;
	}

	@Override
	public boolean isRequireEnumeration() {
		return true;
	}

	@Override
	public Flux<?> apply(Type type, Object value) {
		Enumeration<?> enumeration = (Enumeration<?>) value;

		return Flux.fromIterable(() -> new Iterator<>() {

			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public Object next() {
				return enumeration.nextElement();
			}
		});
	}

}
