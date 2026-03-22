package lu.kbra.pclib.datastructure.tree.dependency;

import java.util.Set;

public interface DependencyOwner<V> {

	Set<V> getDependencies();

	V getKey();

}