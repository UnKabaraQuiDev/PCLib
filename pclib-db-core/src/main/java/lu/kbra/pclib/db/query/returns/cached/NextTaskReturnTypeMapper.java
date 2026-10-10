package lu.kbra.pclib.db.query.returns.cached;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;

public class NextTaskReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public Integer supportsReturnType(Class<?> returnType, HintsOwner owner) {
		return returnType == NextTask.class ? SUPPORTED : NOT_SUPPORTED;
	}

	@Override
	public int getTypeParameterIndex() {
		return 2;
	}

	@Override
	public Object apply(final Query.Type type, final Object value) {
		return NextTask.create(() -> value);
	}

}
