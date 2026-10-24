package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.datastructure.tree.dependency.DependencyOwner;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner.SQLQueryableDependency;

import lombok.Data;
import lombok.EqualsAndHashCode;

public interface SQLQueryableDependencyOwner extends DependencyOwner<SQLQueryableDependency> {

	@Data
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static final class SQLQueryableDependency {

		final Class<? extends SQLQueryable<?>> foreignClass;
		@EqualsAndHashCode.Include
		final String foreignName;

	}

}
