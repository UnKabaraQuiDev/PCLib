package lu.kbra.pclib.db.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface SingleSQLQueryableDependencyOwner extends SQLQueryableDependencyOwner {

	@Override
	default Set<SQLQueryableDependency> getDependencies() {
		return new HashSet<>(Arrays.asList(getDependency()));
	}

	SQLQueryableDependency getDependency();

}
