import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.async.NextTask;

public class NextTaskMain {

	@Test
	public void test() {
		try {
			IntStream.range(0, 35).forEach(i -> {
				NextTask.create(() -> i + " ")
						.catch_(e -> System.out.println("caught: " + i))
						.thenApply(e -> e)
						.catch_(e -> System.out.println("caught 2: " + i))
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

	@Test
	public void test2() {
		NextTask.create(() -> 1)
				.thenCompose(NextTask.collector((n) -> n+1, (n) -> n+1))
				.thenConsume(System.out::println)
				.run();
	}
	
	@Test
	public void test3() {
		NextTask.create(() -> 1)
				.thenCompose(NextTask.collector(
						NextTask.create(() -> 4),
						NextTask.create(() -> 5)
				))
				.thenConsume(System.out::println)
				.run();
	}
	
}
