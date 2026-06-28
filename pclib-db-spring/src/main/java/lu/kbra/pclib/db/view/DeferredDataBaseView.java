package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.view.ViewStructureBuilder;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public class DeferredDataBaseView<T extends DataBaseEntry> extends DataBaseView<T> implements DeferredSQLQueryable<T> {

	public DeferredDataBaseView(final DataBase dataBase) {
		super(dataBase);
	}

	public DeferredDataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public void init(final Class<? extends AbstractDBView<T>> viewClass) {
		super.viewClass = viewClass;
		this.gen_();
	}

	@Override
	protected void gen() {
		// do nothing
	}

	protected void gen_() {
		this.viewStructure = new ViewStructureBuilder<>(this).build();
	}

}
