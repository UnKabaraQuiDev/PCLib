package lu.kbra.pclib.pointer.prim;

import java.util.function.Function;
import java.util.function.Predicate;

import lu.kbra.pclib.pointer.JavaPointer;
import lu.kbra.pclib.pointer.ObjectPointer;

public abstract class PrimitivePointer<T> extends JavaPointer<T> {

	public abstract ObjectPointer<T> toObjectPointer();

	@Override
	public synchronized PrimitivePointer<T> set(final Function<T, T> func) {
		return (PrimitivePointer<T>) super.set(func);
	}

	@Override
	public synchronized boolean isSet() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset(final long timeout) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset(final long timeout, final Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForIsset(final Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset() {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset(final long timeout) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset(final long timeout, final Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	@Override
	public synchronized boolean waitForUnset(final Predicate<T> condition) {
		throw new UnsupportedOperationException("Operation not permitted on primitive pointer !");
	}

	public synchronized String toSafeString() {
		return this.getClass().getSimpleName() + " [value=" + this.get() + "]";
	}

}
