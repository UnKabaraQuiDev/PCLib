package lu.kbra.pclib.db.domain.table;

public interface StructureNameOwner {

	StructureName getStructureName();

	default String getName() {
		return this.getStructureName().getName();
	}

	default String[] getNameParts() {
		return this.getStructureName().getNameParts();
	}

	default String getQualifiedName() {
		return this.getStructureName().getQualifiedName();
	}

}
