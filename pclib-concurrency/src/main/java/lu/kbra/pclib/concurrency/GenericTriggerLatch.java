package lu.kbra.pclib.concurrency;

public interface GenericTriggerLatch<T> {

	void trigger(T value);

}
