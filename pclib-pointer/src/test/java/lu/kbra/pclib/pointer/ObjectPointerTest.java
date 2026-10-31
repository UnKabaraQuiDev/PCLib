package lu.kbra.pclib.pointer;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ObjectPointerTest {

	@Test
	void objectPointerSupportsDefaultsMutationAndMapping() {
		final ObjectPointer<String> pointer = new ObjectPointer<>();
		final AtomicInteger defaults = new AtomicInteger();

		Assertions.assertFalse(pointer.isSet());
		Assertions.assertEquals("fallback", pointer.getOrElse("fallback"));
		Assertions.assertEquals("initial", pointer.getOrElseSet(() -> {
			defaults.incrementAndGet();
			return "initial";
		}));
		Assertions.assertEquals("initial", pointer.getOrElseSet("ignored"));
		Assertions.assertEquals(1, defaults.get());

		pointer.set(current -> current + "-updated");
		pointer.ifSet(value -> Assertions.assertEquals("initial-updated", value));
		Assertions.assertEquals(Integer.valueOf(15), pointer.map(String::length).get());

		pointer.accept(null);
		Assertions.assertFalse(pointer.isSet());
		Assertions.assertNull(pointer.map(String::length).get());
		pointer.orElseSet("restored");
		Assertions.assertEquals("restored", pointer.get());
	}

	@Test
	void stringPointerAppendsValuesAndObjectPointers() {
		final StringPointer pointer = new StringPointer("prefix");

		Assertions.assertSame(pointer, pointer.append("-").append(new ObjectPointer<>(42)));
		Assertions.assertEquals("prefix-42", pointer.get());
		pointer.append((Object) null);
		pointer.append(new ObjectPointer<>());
		Assertions.assertEquals("prefix-42nullnull", pointer.get());
	}

	@Test
	void weakPointerMapsWithoutDependingOnGarbageCollection() {
		final String value = "still-referenced";
		final WeakObjectPointer<String> pointer = new WeakObjectPointer<>(value);

		Assertions.assertTrue(pointer.isSet());
		Assertions.assertSame(value, pointer.get());
		Assertions.assertEquals(Integer.valueOf(value.length()), pointer.map(String::length).get());
		Assertions.assertEquals(Integer.valueOf(value.length()), pointer.weakMap(String::length).get());

		pointer.accept(null);
		Assertions.assertFalse(pointer.isSet());
		Assertions.assertNull(pointer.weakMap(String::length).get());
	}

	@Test
	void predicateWaitReturnsImmediatelyWhenConditionAlreadyMatches() {
		final ObjectPointer<String> pointer = new ObjectPointer<>("ready");
		Assertions.assertTrue(pointer.waitForSet(100, "ready"::equals));
	}

}
