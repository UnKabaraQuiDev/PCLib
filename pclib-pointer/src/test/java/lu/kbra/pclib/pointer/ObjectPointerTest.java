package lu.kbra.pclib.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class ObjectPointerTest {

	@Test
	void objectPointerSupportsDefaultsMutationAndMapping() {
		final ObjectPointer<String> pointer = new ObjectPointer<>();
		final AtomicInteger defaults = new AtomicInteger();

		assertFalse(pointer.isSet());
		assertEquals("fallback", pointer.getOrElse("fallback"));
		assertEquals("initial", pointer.getOrElseSet(() -> {
			defaults.incrementAndGet();
			return "initial";
		}));
		assertEquals("initial", pointer.getOrElseSet("ignored"));
		assertEquals(1, defaults.get());

		pointer.set(current -> current + "-updated");
		pointer.ifSet(value -> assertEquals("initial-updated", value));
		assertEquals(Integer.valueOf(15), pointer.map(String::length).get());

		pointer.accept(null);
		assertFalse(pointer.isSet());
		assertNull(pointer.map(String::length).get());
		pointer.orElseSet("restored");
		assertEquals("restored", pointer.get());
	}

	@Test
	void stringPointerAppendsValuesAndObjectPointers() {
		final StringPointer pointer = new StringPointer("prefix");

		assertSame(pointer, pointer.append("-").append(new ObjectPointer<>(42)));
		assertEquals("prefix-42", pointer.get());
		pointer.append((Object) null);
		pointer.append(new ObjectPointer<>());
		assertEquals("prefix-42nullnull", pointer.get());
	}

	@Test
	void weakPointerMapsWithoutDependingOnGarbageCollection() {
		final String value = new String("still-referenced");
		final WeakObjectPointer<String> pointer = new WeakObjectPointer<>(value);

		assertTrue(pointer.isSet());
		assertSame(value, pointer.get());
		assertEquals(Integer.valueOf(value.length()), pointer.map(String::length).get());
		assertEquals(Integer.valueOf(value.length()), pointer.weakMap(String::length).get());

		pointer.accept(null);
		assertFalse(pointer.isSet());
		assertNull(pointer.weakMap(String::length).get());
	}

	@Test
	void predicateWaitReturnsImmediatelyWhenConditionAlreadyMatches() {
		final ObjectPointer<String> pointer = new ObjectPointer<>("ready");
		assertTrue(pointer.waitForSet(100, "ready"::equals));
	}

}
