package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.view.ViewStructure;

public class ViewRemoved implements SchemaChange {

	private final ViewStructure view;

	public ViewRemoved(final ViewStructure view) {
		this.view = view;
	}

	public ViewStructure getView() {
		return this.view;
	}

}
