import java.util.function.Function;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.cache.CacheManager;
import lu.pcy113.pclib.cache.TimedCacheList;

public class CacheMain {

	@Test
	public void fib() {
		final CacheManager manager = new CacheManager();
		final TimedCacheList<Integer, Integer> fibCache = new TimedCacheList<>(2500);
		manager.addCache("fib", fibCache);

		final Function<Integer, Integer> compute = (a) -> fibCache.getOrPut(a, () -> fibonacci(a));

		fibCache.put(0, 0);
		fibCache.clear();

		final int NMB = 40;

		final long firstTime = PCUtils.nanoTime(() -> compute.apply(NMB));
		System.out.println("First time: " + ((double) firstTime / 1_000_000) + "ms");

		for (int i = 0; i < 50; i++) {
			final int j = i;
			long nt = PCUtils.nanoTime(() -> compute.apply(NMB));
			System.out.println(((double) nt / 1_000_000) + "ms");
			assert nt < firstTime;
		}
	}

	public static int fibonacci(int a) {
		if (a <= 1) {
			return a;
		}
		return fibonacci(a - 1) + fibonacci(a - 2);
	}

}
