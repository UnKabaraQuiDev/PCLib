package lu.kbra.pclib.db.table.transaction;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.NTAbstractDBTable;

public interface NTDBTableTransaction<T extends DataBaseEntry> extends DBTableTransaction<T>, NTAbstractDBTable<T> {

	default NextTask<Void, ?, Void> ntCommit() {
		return NextTask.create(() -> commit());
	}

	default NextTask<Void, ?, Void> ntRollback() {
		return NextTask.create(() -> rollback());
	}

	default NextTask<Void, ?, Void> ntClose() {
		return NextTask.create(() -> close());
	}

	default NextTask<Void, ?, Boolean> ntIsClosed() {
		return NextTask.create(() -> isClosed());
	}

}
