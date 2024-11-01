package lu.pcy113.pclib.db;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import lu.pcy113.pclib.impl.ExceptionFunction;

public abstract class ReturnData<T> {

	public abstract T getData();

	public abstract Exception getException();

	public abstract ReturnStatus getStatus();

	@Override
	public String toString() {
		reportException();
		return "ReturnData{" + "status=" + getStatus() + ", data=" + getData() + ", exception=" + getException() + "}";
	}

	public void reportException() {
		if (getException() != null) {
			getException().printStackTrace(System.err);
		}
	}

	public <N> N multiMap(ExceptionFunction<T, N> ok, ExceptionFunction<Exception, N> error) throws Exception {
		if (error != null && isError())
			return error.apply(getException());
		else if (ok != null && isOk())
			return ok.apply(getData());
		return null;
	}

	public <N> N mapOk(ExceptionFunction<T, N> ok) throws Exception {
		return isOk() ? ok.apply(getData()) : null;
	}

	public <N> N mapError(ExceptionFunction<Exception, N> error) throws Exception {
		return isError() ? error.apply(getException()) : null;
	}

	public <N> ReturnData<N> mapReturnOk(ExceptionFunction<T, N> ok) throws Exception {
		return isOk() ? ok(ok.apply(getData())) : null;
	}

	public boolean isError() {
		return getStatus().equals(ReturnStatus.ERROR);
	}

	public boolean isOk() {
		return getStatus().equals(ReturnStatus.OK);
	}

	public boolean isStatus(ReturnStatus t) {
		return getStatus().equals(t);
	}

	public <U> U apply(BiFunction<ReturnStatus, T, U> cons) {
		return cons.apply(getStatus(), getData());
	}

	public ReturnData<T> ifOk(Consumer<T> cons) {
		if (isStatus(ReturnStatus.OK)) {
			cons.accept(getData());
		}
		return this;
	}

	public ReturnData<T> ifError(Consumer<Exception> cons) {
		if (isStatus(ReturnStatus.ERROR)) {
			cons.accept(getException());
		}
		return this;
	}

	public <U> ReturnData<U> castError() {
		return ReturnData.error(this.getException());
	}

	public static <T> ReturnData<T> ok(T data) {
		return of(data, ReturnStatus.OK);
	}

	public static <T> ReturnData<T> error(Exception data) {
		return of(data, ReturnStatus.ERROR);
	}

	public static <T> ReturnData<T> of(T data, ReturnStatus existed, Exception e) {
		return new ReturnData<T>() {
			@Override
			public T getData() {
				return data;
			}

			@Override
			public ReturnStatus getStatus() {
				return existed;
			}

			@Override
			public Exception getException() {
				return e;
			}
		};
	}

	public static <T> ReturnData<T> of(T data, ReturnStatus created) {
		return new ReturnData<T>() {
			@Override
			public T getData() {
				return data;
			}

			@Override
			public ReturnStatus getStatus() {
				return created;
			}

			@Override
			public Exception getException() {
				return null;
			}
		};
	}

	public static <T> ReturnData<T> of(Exception data, ReturnStatus created) {
		return new ReturnData<T>() {
			@Override
			public Exception getException() {
				return data;
			}

			@Override
			public ReturnStatus getStatus() {
				return created;
			}

			@Override
			public T getData() {
				return null;
			}
		};
	}

	public enum ReturnStatus {
		OK,
		ERROR;
	}

}