package lu.kbra.pclib.db.impl;

import lombok.Data;
import lu.kbra.pclib.datastructure.tree.dependency.DependencyOwner;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner.SQLQueryableDependency;

public interface SQLQueryableDependencyOwner extends DependencyOwner<SQLQueryableDependency> {

	@Data
	public static final class SQLQueryableDependency {

		final Class<? extends SQLQueryable<?>> foreignClass;
		final String foreignName;

	}

}
