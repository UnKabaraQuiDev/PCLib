package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

public interface DatabaseMigration {

	/**
	 * Stable identifier.
	 *
	 * NEVER change this after the migration has been released.
	 */
	String id();

	String name();

	/**
	 * Lower values run first.
	 */
	int order();

	default String description() {
		return this.name();
	}

	DatabaseMigrationPhase[] phases();

	default DatabaseMigrationPhase[] phase(final MigrationPhase phase) {
		return Arrays.stream(this.phases())
				.filter(p -> p.phase() == phase)
				.sorted(Comparator.comparingInt(DatabaseMigrationPhase::order))
				.toArray(DatabaseMigrationPhase[]::new);
	}

	default void validatePhases() {
		final DatabaseMigrationPhase[] phases = this.phases();
		if (phases.length != Arrays.stream(phases).map(DatabaseMigrationPhase::id).distinct().count()) {
			throw new IllegalArgumentException("Duplicate ids in migration phases:\n"
					+ Arrays.stream(phases).map(c -> c.id() + ": " + c.name()).collect(Collectors.joining("\n")));
		}
		if (Arrays.stream(phases).map(DatabaseMigrationPhase::id).filter(c -> c.length() > 64).count() > 0) {
			throw new IllegalArgumentException("Id too long for:\n" + Arrays.stream(phases)
					.filter(c -> c.id().length() > 64)
					.map(c -> c.id() + ": " + c.name())
					.collect(Collectors.joining("\n")));
		}
	}

	public interface DatabaseMigrationPhase {

		String id();

		String name();

		void up(Statement stmt) throws SQLException;

		int order();

		default MigrationPhase phase() {
			return MigrationPhase.MANUAL;
		}

	}

	@AllArgsConstructor
	public abstract static class ManualMigrationPhase implements DatabaseMigrationPhase {

		private final String id;
		private final String name;
		private final int order;
		private final MigrationPhase phase;

		@Override
		public String id() {
			return this.id;
		}

		@Override
		public String name() {
			return this.name;
		}

		@Override
		public int order() {
			return this.order;
		}

		@Override
		public MigrationPhase phase() {
			return this.phase;
		}

	}

}
