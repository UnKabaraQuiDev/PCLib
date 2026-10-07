package lu.kbra.pclib.db.type.returns;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

import reactor.core.publisher.Mono;

@Component
@ConditionalOnClass(Mono.class)
public class MonoReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(Class<?> returnType) {
		return returnType == Mono.class;
	}

	@Override
	public Object apply(final Query.Type type, final Object value) {
		return type.isNullable() ? Mono.justOrEmpty(value) : Mono.just(value);
	}

}
