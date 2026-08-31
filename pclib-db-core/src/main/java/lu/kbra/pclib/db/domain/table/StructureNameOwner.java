package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.db.domain.Qualified;

public interface StructureNameOwner {

	StructureName getStructureName();

	default String getName() {
		return this.getStructureName().getName();
	}

	default String[] getNameParts() {
		return this.getStructureName().getNameParts();
	}

	default @Qualified String getQualifiedName() {
		return this.getStructureName().getQualifiedName();
	}

}
