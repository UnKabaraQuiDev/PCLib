package versioning;

import lu.pcy113.pclib.boot.UpdateChecker;

public class FixedUpdateChecker implements UpdateChecker {

	@Override
	public boolean needsUpdate() {
		return compareVersion(getCurrentVersion(), getLatestVersion()) < 0;
	}

	@Override
	public String getCurrentVersion() {
		return "0.0.1";
	}

	@Override
	public String getLatestVersion() {
		return "0.1.0";
	}

	@Override
	public int compareVersion(String version1, String version2) {
		// Split the version strings into parts
		String[] parts1 = version1.split("\\.");
		String[] parts2 = version2.split("\\.");

		// Determine the maximum length between the two arrays
		int maxLength = Math.max(parts1.length, parts2.length);

		for (int i = 0; i < maxLength; i++) {
			// Parse each part to an integer, default to 0 if a part is missing
			int v1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
			int v2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

			// Compare the parts numerically
			if (v1 < v2) {
				return -1; // version1 is smaller
			} else if (v1 > v2) {
				return 1; // version1 is larger
			}
		}

		// If all parts are equal
		return 0;
	}

}
