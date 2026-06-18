package sqlite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public final class SQLite {

	public static final String DB_NAME = "__testdb";

	public static Path createTempDirectory() throws IOException {
		Files.createDirectories(Paths.get(".local/pclib-sqlite"));
		return Paths.get(".local/pclib-sqlite");
	}

	public static void deleteDirectory(final Path dir) throws IOException {
		if (dir == null || !Files.exists(dir)) {
			return;
		}

		try (java.util.stream.Stream<Path> paths = Files.walk(dir)) {
			paths.sorted(Comparator.reverseOrder()).forEach(path -> {
				try {
					Files.deleteIfExists(path);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to delete " + path, e);
				}
			});
		}
	}

	private SQLite() {
	}

}
