package lu.kbra.pclib;

import java.time.Duration;

import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;

public class TimedResult<T> extends ReadOnlyPair<T, Duration> {

	public TimedResult(T key, Duration value) {
		super(key, value);
	}

}
