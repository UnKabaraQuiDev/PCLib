package lu.kbra.pclib.db.type.returns.cached;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

import reactor.core.publisher.Flux;

@Component
@ConditionalOnClass(Flux.class)
public class CFluxReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(final Class<?> returnType, final HintsOwner hints) {
		return returnType == Flux.class && hints.getBooleanHint(DefaultQueryHints.CACHED, false) ? ReturnTypeMapper.SUPPORTED
				: ReturnTypeMapper.NOT_SUPPORTED;
	}

	@Override
	public Type getDefaultStrategy() {
		return Type.LIST_EMPTY;
	}

	@Override
	public Flux<?> apply(final Type type, final Object value) {
		return Flux.fromStream(((List<?>) value).stream());
	}

}
