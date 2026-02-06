package lu.kbra.pclib.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerTimedCacheList<K, V> extends TimedCacheList<K, V> {

	private ScheduledExecutorService scheduler;

	public SchedulerTimedCacheList(long expirationTimeMillis) {
		super(expirationTimeMillis);
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		startCleanupTask();
	}

	protected void onCleanup() {
	}

	protected void onShutdown() {
	}

	protected void startCleanupTask() {
		scheduler.scheduleAtFixedRate(() -> {
			long now = System.currentTimeMillis();
			cache.entrySet().removeIf(entry -> (now - entry.getValue().getTimestamp()) > expirationTimeMillis);
			onCleanup();
		}, expirationTimeMillis, expirationTimeMillis, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {
		scheduler.shutdown();
		onShutdown();
	}

}
