import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.async.NextTask;

public class NextTaskMain {

	@Test
	public void test() {
		try {
			IntStream.range(0, 35).forEach(i -> {
				NextTask.create(() -> i + " ")
						.catch_(e -> System.out.println("error: " + i))
						.thenApply(e -> e)
						.catch_(e -> System.out.println("error2: " + i))
						.thenApply((e) -> {
							if (Math.random() > 0.5) {
								throw new Exception();
							}
							return e;
						})
						.thenConsume(System.out::println)
					.run();
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
