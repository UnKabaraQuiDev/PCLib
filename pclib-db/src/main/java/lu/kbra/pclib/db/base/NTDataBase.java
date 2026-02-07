package lu.kbra.pclib.db.base;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class NTDataBase extends DataBase {

	@Deprecated
	public NTDataBase(DataBaseConnector connector) {
		super(connector);
	}

	public NTDataBase(DataBaseConnector connector, String name) {
		super(connector, name);
	}

	public NTDataBase(DataBaseConnector connector, String name, String charSet, String collation) {
		super(connector, name, charSet, collation);
	}

	public NextTask<Void, ?, Boolean> ntExists() {
		return NextTask.create(super::exists);
	}

	public NextTask<Void, ?, DataBaseStatus> ntCreate() {
		return NextTask.create(super::create);
	}

	public NextTask<Void, ?, DataBase> ntDrop() {
		return NextTask.create(super::drop);
	}

	@Override
	public String toString() {
		return "NTDataBase@" + System.identityHashCode(this) + " [connector=" + connector + ", dataBaseName=" + dataBaseName + "]";
	}

}
