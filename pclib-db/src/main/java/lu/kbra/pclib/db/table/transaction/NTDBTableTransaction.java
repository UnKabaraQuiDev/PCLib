package lu.kbra.pclib.db.table.transaction;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.impl.ThrowingRunnable;
import lu.kbra.pclib.impl.ThrowingSupplier;

public interface NTDBTableTransaction<T extends DataBaseEntry> extends DBTransaction {

	default NextTask<Void, ?, Void> ntCommit() {
		return NextTask.create((ThrowingRunnable<Throwable>) this::commit);
	}

	default NextTask<Void, ?, Void> ntRollback() {
		return NextTask.create((ThrowingRunnable<Throwable>) this::rollback);
	}

	default NextTask<Void, ?, Void> ntClose() {
		return NextTask.create((ThrowingRunnable<Throwable>) this::close);
	}

	default NextTask<Void, ?, Boolean> ntIsClosed() {
		return NextTask.create((ThrowingSupplier<Boolean, Throwable>) this::isClosed);
	}

}
