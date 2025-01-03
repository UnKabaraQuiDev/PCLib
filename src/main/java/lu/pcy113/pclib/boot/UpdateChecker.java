package lu.pcy113.pclib.boot;

public interface UpdateChecker {

	boolean needsUpdate();

	String getCurrentVersion();

	String getLatestVersion();

	/**
	 * "0.0.1" vs. "0.1.0" → -1 (second version is larger)<br>
	 * "1.0.0" vs. "1.0" → 0 (versions are equivalent)<br>
	 * "2.1" vs. "1.9.9" → 1 (first version is larger)<br>
	 */
	int compareVersion(String v1, String v2);

}
