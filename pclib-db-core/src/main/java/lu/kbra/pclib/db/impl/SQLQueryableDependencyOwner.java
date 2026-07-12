package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.datastructure.tree.dependency.DependencyOwner;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner.SQLQueryableDependency;

import lombok.Data;

public interface SQLQueryableDependencyOwner extends DependencyOwner<SQLQueryableDependency> {

	@Data
	public static final class SQLQueryableDependency {

		final Class<? extends SQLQueryable<?>> foreignClass;
		final String foreignName;

	}

}
