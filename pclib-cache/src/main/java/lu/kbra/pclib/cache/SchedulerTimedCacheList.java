package lu.kbra.pclib.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerTimedCacheList<K, V> extends TimedCacheList<K, V> {

	private final ScheduledExecutorService scheduler;

	public SchedulerTimedCacheList(final long expirationTimeMillis) {
		super(expirationTimeMillis);
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.startCleanupTask();
	}

	protected void onCleanup() {
	}

	protected void onShutdown() {
	}

	protected void startCleanupTask() {
		this.scheduler.scheduleAtFixedRate(() -> {
			final long now = System.currentTimeMillis();
			this.cache.entrySet().removeIf(entry -> now - entry.getValue().getTimestamp() > this.expirationTimeMillis);
			this.onCleanup();
		}, this.expirationTimeMillis, this.expirationTimeMillis, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {
		this.scheduler.shutdown();
		this.onShutdown();
	}

}
