package lu.kbra.pclib;

import java.time.Duration;

import lu.kbra.pclib.datastructure.tuple.ReadOnlyPairImpl;

public class TimedResult<T> extends ReadOnlyPairImpl<T, Duration> {

	public TimedResult(T key, Duration value) {
		super(key, value);
	}

}
