package lu.kbra.pclib.db.domain.table;

public interface StructureNameOwner {

	StructureName getStructureName();

	default String getName() {
		return getStructureName().getName();
	}

	default String[] getNameParts() {
		return getStructureName().getNameParts();
	}

	default String getQualifiedName() {
		return getStructureName().getQualifiedName();
	}

}
