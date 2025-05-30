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
		Assertions.assertEquals("This is a string append", NextTask.create(() -> "This is a string").thenApply((s) -> s + " append").run());
	}

	@Test
	public void level2apply() {
		Assertions.assertEquals("This is a string append 2x", NextTask.create(() -> "This is a string").thenApply((s) -> s + " append").thenApply((s) -> s + " 2x").run());
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

	/*
	 * @Test public void doubleCatcher() {
	 * Assertions.assertThrows(IllegalStateException.class, () -> NextTask.create(()
	 * -> { throw new RuntimeException("This is an error"); }).catch_(e -> {
	 * System.out.println("(1) there was an error"); }).catch_(e -> {
	 * System.out.println("(2) there was an error"); }).run());
	 * 
	 * assertOutput("(2) there was an error",
	 * "(2) Exception wasn't caught properly");
	 * assertOutput("(1) there was an error",
	 * "(1) Exception wasn't caught properly"); }
	 */

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
		NextTask.create(() -> 1).thenCompose(NextTask.chain((n) -> n + 1, (n) -> n + 1)).thenParallel(Collections::sort).thenConsume(System.out::println).run();

		assertOutput("[1, 2, 3]", (s) -> "Unexpected result: " + s);
	}

	@Test
	public void composeCollector() {
		NextTask.create(() -> 1).thenCompose(NextTask.collector(NextTask.create(() -> 4), NextTask.create(() -> 5))).thenParallel(Collections::sort).thenConsume(System.out::println).run();

		assertOutput("[1, 4, 5]", (s) -> "Unexpected result: " + s);
	}

	@Test
	public void composeParallel() {
		NextTask.create(() -> 1).thenCompose(NextTask.parallel(NextTask.withArg((n) -> n + 1), NextTask.withArg((n) -> n + 2))).thenConsume(System.out::println).run();

		assertOutput("[2, 3]", (s) -> "Unexpected result: " + s);
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
