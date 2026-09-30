package lu.kbra.pclib.db.query.returns;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.annotations.query.Query;

public class NextTaskReturnTypeMapper implements ReturnTypeMapper {

	@Override
	public boolean supportsReturnType(final Class<?> returnType) {
		return returnType == NextTask.class;
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
