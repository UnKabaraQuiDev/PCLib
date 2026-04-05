package lu.kbra.pclib.pointer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class JavaPointer<T> implements Consumer<T>, Supplier<T> {

	public abstract boolean isSet();

	@Override
	public synchronized void accept(T t) {
		this.set(t);
	}

	public synchronized void ifSet(final Consumer<T> action) {
		if (this.isSet()) {
			action.accept(this.get());
		}
	}

	public synchronized JavaPointer<T> orElseSet(final T other) {
		if (!this.isSet()) {
			this.set(other);
		}
		return this;
	}

	public synchronized T getOrElse(final T other) {
		return this.isSet() ? this.get() : other;
	}

	public synchronized T getOrElseSet(final T other) {
		if (!this.isSet()) {
			this.set(other);
		}
		return this.get();
	}

	public synchronized JavaPointer<T> orElseSet(final Supplier<T> action) {
		if (!this.isSet()) {
			this.set(action.get());
		}
		return this;
	}

	public synchronized T getOrElse(final Supplier<T> action) {
		return this.isSet() ? this.get() : action.get();
	}

	public synchronized T getOrElseSet(final Supplier<T> action) {
		if (!this.isSet()) {
			this.set(action.get());
		}
		return this.get();
	}

	public synchronized JavaPointer<T> set(final Function<T, T> func) {
		return this.set(func.apply(this.get()));
	}

	public synchronized boolean waitForSet() {
		try {
			this.wait();
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	/**
	 * @param timeout is ms
	 * @return
	 * @return true if the pointer was set before the timeout expired
	 */
	public synchronized boolean waitForSet(final long timeout) {
		try {
			this.wait(timeout);
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForSet(final Predicate<T> condition) {
		try {
			while (!condition.test(this.get())) {
				this.wait();
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	/**
	 * @param timeout   is ms
	 * @param condition
	 * @return true is the condition was met before the timeout
	 */
	public synchronized boolean waitForSet(final long timeout, final Predicate<T> condition) {
		try {
			final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);
			while (!condition.test(this.get())) {
				final long remaining = deadline - System.nanoTime();
				if (remaining <= 0) {
					return false;
				}
				this.wait(remaining);
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForChange() {
		final T initialValue = this.get();
		return this.waitForSet(v -> !Objects.equals(v, initialValue));
	}

	public synchronized boolean waitForChange(final long timeout) {
		final T initialValue = this.get();
		return this.waitForSet(timeout, v -> !Objects.equals(v, initialValue));
	}

	public synchronized boolean waitForChange(final Predicate<T> condition) {
		final T initialValue = this.get();
		return this.waitForSet(v -> !Objects.equals(v, initialValue) && condition.test(v));
	}

	public synchronized boolean waitForChange(final long timeout, final Predicate<T> condition) {
		final T initialValue = this.get();
		return this.waitForSet(timeout, v -> !Objects.equals(v, initialValue) && condition.test(v));
	}

	public synchronized boolean waitForUnset() {
		try {
			while (this.isSet()) {
				this.wait();
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForUnset(final long timeout) {
		try {
			while (this.isSet()) {
				this.wait(timeout);
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForUnset(final Predicate<T> condition) {
		try {
			while (this.isSet() && condition.test(this.get())) {
				this.wait();
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForUnset(final long timeout, final Predicate<T> condition) {
		try {
			final long startTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);
			while (this.isSet() && condition.test(this.get())) {
				final long elapsed = System.nanoTime() - startTime;
				if (elapsed >= timeout) {
					return false;
				}
				this.wait(timeout - elapsed);
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset() {
		try {
			while (!this.isSet()) {
				this.wait();
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset(final long timeout) {
		try {
			while (!this.isSet()) {
				this.wait(timeout);
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset(final Predicate<T> condition) {
		try {
			while (!this.isSet() || !condition.test(this.get())) {
				this.wait();
			}
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public synchronized boolean waitForIsset(final long timeout, final Predicate<T> condition) {
		try {
			final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);
			final long remaining = deadline - System.nanoTime();
			if (remaining <= 0) {
				return false;
			}
			this.wait(remaining);
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public abstract T get();

	public abstract JavaPointer<T> set(T value);

}
