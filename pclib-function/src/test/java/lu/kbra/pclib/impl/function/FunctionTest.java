package lu.kbra.pclib.impl.function;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FunctionTest {

	@Test
	void ordinaryFunctionsApplyAllArgumentsAndComposeResults() {
		final TriFunction<Integer, Integer, Integer, Integer> tri = (a, b, c) -> a + b + c;
		final QuadFunction<Integer, Integer, Integer, Integer, Integer> quad = (a, b, c, d) -> a + b + c + d;
		final QuintFunction<Integer, Integer, Integer, Integer, Integer, Integer> quint = (a, b, c, d, e) -> a + b + c + d + e;
		final SextFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer> sext = (a, b, c, d, e, f) -> a + b + c + d + e
				+ f;
		final SeptFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> sept = (a, b, c, d, e, f, g) -> a + b + c
				+ d + e + f + g;
		final OctFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> oct = (
				a,
				b,
				c,
				d,
				e,
				f,
				g,
				h) -> a + b + c + d + e + f + g + h;

		Assertions.assertEquals(Integer.valueOf(12), tri.andThen(n -> n * 2).apply(1, 2, 3));
		Assertions.assertEquals(Integer.valueOf(20), quad.andThen(n -> n * 2).apply(1, 2, 3, 4));
		Assertions.assertEquals(Integer.valueOf(30), quint.andThen(n -> n * 2).apply(1, 2, 3, 4, 5));
		Assertions.assertEquals(Integer.valueOf(42), sext.andThen(n -> n * 2).apply(1, 2, 3, 4, 5, 6));
		Assertions.assertEquals(Integer.valueOf(56), sept.andThen(n -> n * 2).apply(1, 2, 3, 4, 5, 6, 7));
		Assertions.assertEquals(Integer.valueOf(72), oct.andThen(n -> n * 2).apply(1, 2, 3, 4, 5, 6, 7, 8));
	}

	@Test
	void throwingFunctionsComposeAndAdaptToNonThrowingInterfaces() throws IOException {
		final ThrowingFunction<Integer, Integer, IOException> one = a -> a + 1;
		final ThrowingBiFunction<Integer, Integer, Integer, IOException> two = (a, b) -> a + b;
		final ThrowingTriFunction<Integer, Integer, Integer, Integer, IOException> three = (a, b, c) -> a + b + c;
		final ThrowingQuadFunction<Integer, Integer, Integer, Integer, Integer, IOException> four = (a, b, c, d) -> a + b + c + d;
		final ThrowingQuintFunction<Integer, Integer, Integer, Integer, Integer, Integer, IOException> five = (a, b, c, d, e) -> a + b + c
				+ d + e;
		final ThrowingSextFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, IOException> six = (a, b, c, d, e, f) -> a
				+ b + c + d + e + f;
		final ThrowingSeptFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, IOException> seven = (
				a,
				b,
				c,
				d,
				e,
				f,
				g) -> a + b + c + d + e + f + g;
		final ThrowingOctFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, IOException> eight = (
				a,
				b,
				c,
				d,
				e,
				f,
				g,
				h) -> a + b + c + d + e + f + g + h;

		Assertions.assertEquals(Integer.valueOf(4), one.andThen(n -> n * 2).apply(1));
		Assertions.assertEquals(Integer.valueOf(6), two.andThen(n -> n * 2).apply(1, 2));
		Assertions.assertEquals(Integer.valueOf(12), three.andThen(n -> n * 2).apply(1, 2, 3));
		Assertions.assertEquals(Integer.valueOf(20), four.andThen(n -> n * 2).apply(1, 2, 3, 4));
		Assertions.assertEquals(Integer.valueOf(30), five.andThen(n -> n * 2).apply(1, 2, 3, 4, 5));
		Assertions.assertEquals(Integer.valueOf(42), six.andThen(n -> n * 2).apply(1, 2, 3, 4, 5, 6));
		Assertions.assertEquals(Integer.valueOf(56), seven.andThen(n -> n * 2).apply(1, 2, 3, 4, 5, 6, 7));
		Assertions.assertEquals(Integer.valueOf(72), eight.andThen(n -> n * 2).apply(1, 2, 3, 4, 5, 6, 7, 8));

		Assertions.assertEquals(Integer.valueOf(2), one.asRuntime().apply(1));
		Assertions.assertEquals(Integer.valueOf(3), two.asRuntime().apply(1, 2));
		Assertions.assertEquals(Integer.valueOf(6), three.asRuntime().apply(1, 2, 3));
		Assertions.assertEquals(Integer.valueOf(10), four.asRuntime().apply(1, 2, 3, 4));
		Assertions.assertEquals(Integer.valueOf(15), five.asRuntime().apply(1, 2, 3, 4, 5));
		Assertions.assertEquals(Integer.valueOf(21), six.asRuntime().apply(1, 2, 3, 4, 5, 6));
		Assertions.assertEquals(Integer.valueOf(28), seven.asRuntime().apply(1, 2, 3, 4, 5, 6, 7));
		Assertions.assertEquals(Integer.valueOf(36), eight.asRuntime().apply(1, 2, 3, 4, 5, 6, 7, 8));
	}

	@Test
	void runtimeAdaptersWrapCheckedExceptionsButPreserveRuntimeExceptions() {
		final IOException checked = new IOException("checked");
		final ThrowingFunction<String, String, IOException> checkedFunction = value -> {
			throw checked;
		};
		final RuntimeException wrapped = Assertions.assertThrows(RuntimeException.class, () -> checkedFunction.asRuntime().apply("value"));
		Assertions.assertSame(checked, wrapped.getCause());

		final IllegalArgumentException unchecked = new IllegalArgumentException("unchecked");
		final ThrowingBiFunction<String, String, String, IOException> uncheckedFunction = (a, b) -> {
			throw unchecked;
		};
		Assertions.assertSame(unchecked,
				Assertions.assertThrows(IllegalArgumentException.class, () -> uncheckedFunction.asRuntime().apply("a", "b")));
	}

	@Test
	void andThenRejectsNullFunction() {
		final TriFunction<Integer, Integer, Integer, Integer> ordinary = (a, b, c) -> a + b + c;
		final ThrowingFunction<Integer, Integer, IOException> throwing = n -> n;
		Assertions.assertThrows(NullPointerException.class, () -> ordinary.andThen(null));
		Assertions.assertThrows(NullPointerException.class, () -> throwing.andThen(null));
	}

}
