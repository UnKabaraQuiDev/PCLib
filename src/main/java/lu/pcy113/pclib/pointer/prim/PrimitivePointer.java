package lu.pcy113.pclib.pointer.prim;

import lu.pcy113.pclib.pointer.JavaPointer;
import lu.pcy113.pclib.pointer.ObjectPointer;

public abstract class PrimitivePointer<T> implements JavaPointer<T> {

	public abstract ObjectPointer<T> toObjectPointer();
	
	@Override
	public boolean isSet() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

}
