package lu.pcy113.pclib.concurrency;

public interface GenericTriggerLatch<T> {

	void trigger(T value);

}
