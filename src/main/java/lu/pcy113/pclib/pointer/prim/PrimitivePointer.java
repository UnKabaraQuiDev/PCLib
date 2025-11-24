package lu.pcy113.pclib.pointer.prim;

import java.util.function.Predicate;

import lu.pcy113.pclib.pointer.JavaPointer;
import lu.pcy113.pclib.pointer.ObjectPointer;

public abstract class PrimitivePointer<T> extends JavaPointer<T> {

	public abstract ObjectPointer<T> toObjectPointer();

	@Override
	public synchronized boolean isSet() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset(long timeout) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset(long timeout, Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset(Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset(long timeout) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset(long timeout, Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset(Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	public synchronized String toSafeString() {
		return this.getClass().getSimpleName() + " [value=" + get() + "]";
	}

}
