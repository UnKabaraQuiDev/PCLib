import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.async.NextTask;

public class NextTaskMain {

	@Test
	public void test() {
		try {
			IntStream.range(0, 35).forEach(i -> {
				NextTask.create(() -> i+" ").thenConsume(System.out::print).runAsync();
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
