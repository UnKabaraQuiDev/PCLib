package lu.kbra.pclib.db.autobuild.view;

import java.util.ArrayList;
import java.util.List;

public class UnionTableStructure {

	private String name;
	private String resolvedTypeName;
	private final List<ViewColumnStructure> columns = new ArrayList<>();

	public UnionTableStructure(final String name, final String resolvedTypeName) {
		this.name = name;
		this.resolvedTypeName = resolvedTypeName;
	}

	public UnionTableStructure() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getResolvedTypeName() {
		return this.resolvedTypeName;
	}

	public void setResolvedTypeName(final String resolvedTypeName) {
		this.resolvedTypeName = resolvedTypeName;
	}

	public List<ViewColumnStructure> getColumns() {
		return this.columns;
	}

	public String getEffectiveName() {
		return this.name != null && !this.name.trim().isEmpty() ? this.name : this.resolvedTypeName;
	}

	@Override
	public String toString() {
		return "UnionTableStructure@" + System.identityHashCode(this) + " [name=" + this.name + ", resolvedTypeName="
				+ this.resolvedTypeName + ", columns=" + this.columns + "]";
	}

}
