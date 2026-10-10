package lu.kbra.pclib.db.type.returns.streaming;

import java.util.Enumeration;
import java.util.Iterator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query.Type;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

import reactor.core.publisher.Flux;

@Component
@ConditionalOnClass(Flux.class)
public class SFluxReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(final Class<?> returnType, final HintsOwner hints) {
		return returnType == Flux.class && !hints.getBooleanHint(DefaultQueryHints.CACHED, false) ? ReturnTypeMapper.SUPPORTED
				: ReturnTypeMapper.NOT_SUPPORTED;
	}

	@Override
	public boolean isRequireEnumeration() {
		return true;
	}

	@Override
	public Flux<?> apply(final Type type, final Object value) {
		final Enumeration<?> enumeration = (Enumeration<?>) value;

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
