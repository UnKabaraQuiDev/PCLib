package lu.pcy113.pclib.pointer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class JavaPointer<T> {

	public abstract boolean isSet();

	public synchronized JavaPointer<T> set(Function<T, T> func) {
		return set(func.apply(get()));
	}

	public synchronized boolean waitForSet() {
		try {
			this.wait();
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	/**
	 * @param timeout is ms
	 * @return
	 * @return true if the pointer was set before the timeout expired
	 */
	public synchronized boolean waitForSet(long timeout) {
		try {
			this.wait(timeout);
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForSet(Predicate<T> condition) {
		try {
			while (!condition.test(get())) {
				this.wait();
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	/**
	 * @param timeout   is ms
	 * @param condition
	 * @return true is the condition was met before the timeout
	 */
	public synchronized boolean waitForSet(long timeout, Predicate<T> condition) {
		try {
			final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);
			while (!condition.test(get())) {
				final long remaining = deadline - System.nanoTime();
				if (remaining <= 0) {
					return false;
				}
				this.wait(remaining);
			}
			return true;
		} catch (InterruptedException e) {
			System.err.println(Thread.currentThread().getName()+" got interrupted");
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForChange() {
		final T initialValue = get();
		return waitForSet(v -> !Objects.equals(v, initialValue));
	}

	public synchronized boolean waitForChange(long timeout) {
		final T initialValue = get();
		return waitForSet(timeout, v -> !Objects.equals(v, initialValue));
	}

	public synchronized boolean waitForChange(Predicate<T> condition) {
		final T initialValue = get();
		return waitForSet(v -> !Objects.equals(v, initialValue) && condition.test(v));
	}

	public synchronized boolean waitForChange(long timeout, Predicate<T> condition) {
		final T initialValue = get();
		return waitForSet(timeout, v -> !Objects.equals(v, initialValue) && condition.test(v));
	}

	public synchronized boolean waitForUnset() {
		try {
			while (isSet()) {
				this.wait();
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForUnset(long timeout) {
		try {
			while (isSet()) {
				this.wait(timeout);
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForUnset(Predicate<T> condition) {
		try {
			while (isSet() && condition.test(get())) {
				this.wait();
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForUnset(long timeout, Predicate<T> condition) {
		try {
			final long startTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);
			while (isSet() && condition.test(get())) {
				final long elapsed = System.nanoTime() - startTime;
				if (elapsed >= timeout) {
					return false;
				}
				this.wait(timeout - elapsed);
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset() {
		try {
			while (!isSet()) {
				this.wait();
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset(long timeout) {
		try {
			while (!isSet()) {
				this.wait(timeout);
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset(Predicate<T> condition) {
		try {
			while (!isSet() || !condition.test(get())) {
				this.wait();
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset(long timeout, Predicate<T> condition) {
		try {
			final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);
			final long remaining = deadline - System.nanoTime();
			if (remaining <= 0)
				return false;
			this.wait(remaining);
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public abstract T get();

	public abstract JavaPointer<T> set(T value);

}
