package lu.kbra.pclib;

@FunctionalInterface
public interface CloneSupplier<T> {

	T get() throws CloneNotSupportedException;

}
