package lu.pcy113.pclib.impl;

@FunctionalInterface
public interface ThrowingSupplier<R, T extends Throwable> {

	R get() throws T;

}
