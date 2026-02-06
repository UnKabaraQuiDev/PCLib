package lu.kbra.pclib.cache;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class CacheManager {

	private Map<String, CacheList> caches = new HashMap<>();

	public CacheManager() {
		this.caches = new HashMap<>();
	}

	public void addCache(String name, CacheList cache) {
		this.caches.put(name, cache);
	}

	public CacheList getCache(String name) {
		return this.caches.get(name);
	}

	public void removeCache(String name) {
		this.caches.remove(name);
	}

	public void clear() {
		this.caches.clear();
	}

}
