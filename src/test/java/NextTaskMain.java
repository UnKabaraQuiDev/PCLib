import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.async.NextTaskSkip;

public class NextTaskMain {

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(originalOut);
	}

	@Test
	public void create_supplier() {
		Assertions.assertEquals("This is a string", NextTask.create(() -> "This is a string").run());
	}

	@Test
	public void withArg_function() {
		Assertions.assertEquals("This is a string", NextTask.withArg((s) -> s + " a string").run("This is"));
	}

	@Test
	public void level1apply() {
		Assertions.assertEquals("This is a string append",
				NextTask.create(() -> "This is a string").thenApply((s) -> s + " append").run());
	}

	@Test
	public void level2apply() {
		Assertions.assertEquals("This is a string append 2x", NextTask.create(() -> "This is a string")
				.thenApply((s) -> s + " append").thenApply((s) -> s + " 2x").run());
	}

	@Test
	public void level1catcher() {
		Assertions.assertDoesNotThrow(() -> NextTask.create(() -> {
			throw new RuntimeException("This is an error");
		}).catch_(e -> {
			System.out.println("there was an error");
		}).run());

		assertOutput("there was an error", "Exception wasn't caught properly");
	}

	@Test
	public void level2catcher() {
		Assertions.assertDoesNotThrow(() -> NextTask.<String>create(() -> {
			// throw new RuntimeException("This is an error");
			return "This is a string";
		}).catch_(e -> {
			System.out.println("(1) there was an error");
		}).thenApply((s) -> {
			throw new RuntimeException("This is an error");
		}).catch_(e -> {
			System.out.println("(2) there was an error");
		}).run());

		assertOutput("(2) there was an error", "(2) Exception wasn't caught properly");
	}

	@Test
	public void higherLevelCatcher() {
		Assertions.assertDoesNotThrow(() -> NextTask.<String>create(() -> {
			throw new RuntimeException("This is an error");
		}).thenApply((s) -> s).catch_(e -> {
			System.out.println("(2) there was an error");
		}).run());

		assertOutput("(2) there was an error", "(2) Exception wasn't caught properly");
	}

	@Test
	public void level3catcher() {
		Assertions.assertDoesNotThrow(() -> NextTask.<String>create(() -> {
			// throw new RuntimeException("This is an error");
			return "This is a string";
		}).catch_(e -> {
			System.out.println("(1) there was an error");
		}).thenApply((s) -> s + " append").thenApply((s) -> {
			throw new RuntimeException("This is an error");
		}).catch_(e -> {
			System.out.println("(2) there was an error");
		}).run());

		assertOutput("(2) there was an error", "(2) Exception wasn't caught properly");
	}

	@Test
	public void level1throw_RuntimeException() {
		Assertions.assertThrows(RuntimeException.class, () -> NextTask.create(() -> {
			throw new RuntimeException("This is an error");
		}).run());
	}

	@Test
	public void level1throw_Exception() {
		Assertions.assertThrows(Exception.class, () -> NextTask.create(() -> {
			throw new Exception("This is an error");
		}).run());
	}

	@Test
	public void chain() {
		NextTask.create(() -> 1).thenCompose(NextTask.chain((n) -> n + 1, (n) -> n + 1)).thenParallel(Collections::sort)
				.thenConsume(System.out::println).run();

		assertOutput("[1, 2, 3]", (s) -> "Unexpected result: " + s);
	}

	@Test
	public void composeCollector() {
		NextTask.create(() -> 1).thenCompose(NextTask.collector(NextTask.create(() -> 4), NextTask.create(() -> 5)))
				.thenParallel(Collections::sort).thenConsume(System.out::println).run();

		assertOutput("[1, 4, 5]", (s) -> "Unexpected result: " + s);
	}

	@Test
	public void composeParallel() {
		NextTask.create(() -> 1)
				.thenCompose(NextTask.parallel(NextTask.withArg((n) -> n + 1), NextTask.withArg((n) -> n + 2)))
				.thenConsume(System.out::println).run();

		assertOutput("[2, 3]", (s) -> "Unexpected result: " + s);
	}

	@Test
	public void skipSingleNode() {
		String result = NextTask.create(() -> "start").thenApply(s -> {
			throw new NextTaskSkip(1, "skipped");
		}).thenApply(s -> s + " end").run();

		Assertions.assertEquals("skipped", result, "The skipped node should use NextTaskSkip object (" + result + ")");
	}

	@Test
	public void skipMultipleNodes() {
		String result = NextTask.create(() -> "start").thenApply(s -> {
			throw new NextTaskSkip(2, "skipped");
		}).thenApply(s -> s + " middle").thenApply(s -> s + " end 1").thenApply(s -> s + " end").run();

		Assertions.assertEquals("skipped end", result,
				"NextTaskSkip should skip 2 nodes and continue (" + result + ")");
	}

	@Test
	public void skipWithInjectedNext() {
		NextTask<String, String, String> injected = NextTask.withArg((String s) -> "injected")
				.thenApply(s -> s + " task");

		String result = NextTask.create(() -> "start").thenApply(s -> {
			throw new NextTaskSkip(1, "skipped", injected);
		}).thenApply(s -> s + " middle").thenApply(s -> s + " end").run();

		Assertions.assertEquals("injected task end", result, "Injected task should be executed after skip");
	}

	@Test
	public void skipNestedSubList() {
		NextTask<Integer, Integer, Integer> subList = NextTask.withArg((Integer i) -> 10).thenApply(n -> n + 1)
				.thenApply(n -> n * 2); // 22

		Integer result = NextTask.create(() -> 5).<Integer>thenApply(n -> {
			throw new NextTaskSkip(1, 0, subList);
		}).thenApply(n -> n + 100).run();

		Assertions.assertEquals(subList.run(), result, "Injected sublist should execute in place of skipped node");
	}

	@Test
	public void skipBeyondChainThrowsNextTaskSkip() {
		NextTaskSkip thrown = Assertions.assertThrows(NextTaskSkip.class, () -> {
			NextTask.create(() -> "start").thenApply(s -> {
				throw new NextTaskSkip(5, "fail");
			}).thenApply(s -> s + " middle").thenApply(s -> s + " end").run();
		});

		Assertions.assertEquals(3, thrown.getCount(), "Remaining skip count should reflect unskipped nodes");
		Assertions.assertEquals("fail", thrown.getObj(), "NextTaskSkip object should propagate correctly");
	}

	private boolean assertOutput(String txt, String err) {
		String consoleOutput = outputStream.toString().trim();
		boolean equals = txt.equals(consoleOutput);
		System.err.println(PCUtils.getCallerClassName(true) + " >> " + consoleOutput);
		assert equals : err;
		return equals;
	}

	private boolean assertOutput(String txt, Function<String, String> err) {
		String consoleOutput = outputStream.toString().trim();
		boolean equals = txt.equals(consoleOutput);
		System.err.println(PCUtils.getCallerClassName(true) + " >> " + consoleOutput);
		assert equals : err.apply(consoleOutput);
		return equals;
	}

}
