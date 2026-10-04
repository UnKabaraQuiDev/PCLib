package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.view.ViewStructure;

public class ViewAdded implements SchemaChange {

	private final ViewStructure view;

	public ViewAdded(final ViewStructure view) {
		this.view = view;
	}

	public ViewStructure getView() {
		return this.view;
	}

}
