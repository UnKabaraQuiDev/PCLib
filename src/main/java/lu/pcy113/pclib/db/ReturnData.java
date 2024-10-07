package lu.pcy113.pclib.db;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class ReturnData<T> {

	public abstract T getData();

	public abstract ReturnStatus getStatus();

	@Override
	public String toString() {
		return "ReturnData{" + "status=" + getStatus() + "," + "data=" + getData() + '}';
	}

	public <U> U apply(BiFunction<ReturnStatus, T, U> cons) {
		return cons.apply(getStatus(), getData());
	}

	public void ifFine(Consumer<T> cons) {
		if (getStatus().equals(ReturnStatus.FINE)) {
			cons.accept(getData());
		}
	}

	public void ifCreated(Consumer<T> cons) {
		if (getStatus().equals(ReturnStatus.CREATED)) {
			cons.accept(getData());
		}
	}

	public void ifExisted(Consumer<T> cons) {
		if (getStatus().equals(ReturnStatus.EXISTED)) {
			cons.accept(getData());
		}
	}

	public void ifError(Consumer<T> cons) {
		if (getStatus().equals(ReturnStatus.ERROR)) {
			cons.accept(getData());
		}
	}

	public void ifStatus(ReturnStatus t, Consumer<T> cons) {
		if (getStatus().equals(t)) {
			cons.accept(getData());
		}
	}

	public static <T> ReturnData<T> fine(T data) {
		return of(data, ReturnStatus.FINE);
	}

	public static <T> ReturnData<T> created(T data) {
		return of(data, ReturnStatus.CREATED);
	}

	public static <T> ReturnData<T> existed(T data) {
		return of(data, ReturnStatus.EXISTED);
	}

	public static <T> ReturnData<T> error(T data) {
		return of(data, ReturnStatus.ERROR);
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
		};
	}

	public enum ReturnStatus {
		FINE, CREATED, EXISTED, ERROR;
	}

}