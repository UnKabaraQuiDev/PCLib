package lu.kbra.pclib.db.base;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

@Deprecated
public class NTDataBase extends DataBase {

	@Deprecated
	public NTDataBase(final DataBaseConnector connector, final String name) {
		super(connector, name);
	}

	@Deprecated
	public NTDataBase(final DataBaseConnector connector, final String name, final String charSet, final String collation) {
		super(connector, name, charSet, collation);
	}

	@Deprecated
	public NextTask<Void, ?, Boolean> ntExists() {
		return NextTask.create(super::exists);
	}

	@Deprecated
	public NextTask<Void, ?, DataBaseStatus> ntCreate() {
		return NextTask.create(super::create);
	}

	@Deprecated
	public NextTask<Void, ?, DataBase> ntDrop() {
		return NextTask.create(super::drop);
	}

	@Deprecated
	@Override
	public String toString() {
		return "NTDataBase@" + System.identityHashCode(this) + " [connector=" + this.connector + ", dataBaseName=" + this.dataBaseName
				+ "]";
	}

}
