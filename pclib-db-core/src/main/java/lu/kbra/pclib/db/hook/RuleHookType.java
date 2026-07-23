package lu.kbra.pclib.db.hook;

public enum RuleHookType {

	PREPARE_INSERT,
	BEFORE_INSERT,
	DURING_INSERT,
	AFTER_INSERT,

	PREPARE_LOAD,
	BEFORE_LOAD,
	DURING_LOAD,
	AFTER_LOAD,

	PREPARE_UPDATE,
	BEFORE_UPDATE,
	AFTER_UPDATE,

	PREPARE_QUERY,
	BEFORE_QUERY,
	DURING_QUERY,
	AFTER_QUERY,

	PREPARE_TRUNCATE,
	BEFORE_TRUNCATE,
	AFTER_TRUNCATE,

	PREPARE_CLEAR,
	BEFORE_CLEAR,
	AFTER_CLEAR,

	PREPARE_COUNT,
	BEFORE_COUNT,
	AFTER_COUNT,

	PREPARE_CREATE,
	BEFORE_CREATE,
	AFTER_CREATE,

	PREPARE_DELETE,
	BEFORE_DELETE,
	AFTER_DELETE,

	PREPARE_DROP,
	BEFORE_DROP,
	AFTER_DROP,

	PREPARE_EXISTS,
	BEFORE_EXISTS,
	AFTER_EXISTS;

	public boolean isPrepare() {
		switch (this) {
		case PREPARE_QUERY:
		case PREPARE_LOAD:
		case PREPARE_UPDATE:
		case PREPARE_INSERT:
		case PREPARE_CLEAR:
		case PREPARE_COUNT:
		case PREPARE_CREATE:
		case PREPARE_DELETE:
		case PREPARE_EXISTS:
		case PREPARE_TRUNCATE:
		case PREPARE_DROP:
			return true;
		default:
			return false;
		}
	}

	public boolean isBefore() {
		switch (this) {
		case BEFORE_INSERT:
		case BEFORE_LOAD:
		case BEFORE_UPDATE:
		case BEFORE_QUERY:
		case BEFORE_TRUNCATE:
		case BEFORE_CLEAR:
		case BEFORE_COUNT:
		case BEFORE_CREATE:
		case BEFORE_DELETE:
		case BEFORE_DROP:
		case BEFORE_EXISTS:
			return true;
		default:
			return false;
		}
	}

	public boolean isDuring() {
		switch (this) {
		case DURING_INSERT:
		case DURING_LOAD:
		case DURING_QUERY:
			return true;
		default:
			return false;
		}
	}

	public boolean isAfter() {
		switch (this) {
		case AFTER_INSERT:
		case AFTER_LOAD:
		case AFTER_UPDATE:
		case AFTER_QUERY:
		case AFTER_TRUNCATE:
		case AFTER_CLEAR:
		case AFTER_COUNT:
		case AFTER_CREATE:
		case AFTER_DELETE:
		case AFTER_DROP:
		case AFTER_EXISTS:
			return true;
		default:
			return false;
		}
	}

	public boolean isInsert() {
		switch (this) {
		case PREPARE_INSERT:
		case BEFORE_INSERT:
		case DURING_INSERT:
		case AFTER_INSERT:
			return true;
		default:
			return false;
		}
	}

	public boolean isLoad() {
		switch (this) {
		case PREPARE_LOAD:
		case BEFORE_LOAD:
		case DURING_LOAD:
		case AFTER_LOAD:
			return true;
		default:
			return false;
		}
	}

	public boolean isUpdate() {
		switch (this) {
		case PREPARE_UPDATE:
		case BEFORE_UPDATE:
		case AFTER_UPDATE:
			return true;
		default:
			return false;
		}
	}

	public boolean isQuery() {
		switch (this) {
		case PREPARE_QUERY:
		case BEFORE_QUERY:
		case DURING_QUERY:
		case AFTER_QUERY:
			return true;
		default:
			return false;
		}
	}

	public boolean isTruncate() {
		switch (this) {
		case PREPARE_TRUNCATE:
		case BEFORE_TRUNCATE:
		case AFTER_TRUNCATE:
			return true;
		default:
			return false;
		}
	}

	public boolean isClear() {
		switch (this) {
		case PREPARE_CLEAR:
		case BEFORE_CLEAR:
		case AFTER_CLEAR:
			return true;
		default:
			return false;
		}
	}

	public boolean isCount() {
		switch (this) {
		case BEFORE_COUNT:
		case AFTER_COUNT:
		case PREPARE_COUNT:
			return true;
		default:
			return false;
		}
	}

	public boolean isCreate() {
		switch (this) {
		case PREPARE_CREATE:
		case BEFORE_CREATE:
		case AFTER_CREATE:
			return true;
		default:
			return false;
		}
	}

	public boolean isDelete() {
		switch (this) {
		case PREPARE_DELETE:
		case BEFORE_DELETE:
		case AFTER_DELETE:
			return true;
		default:
			return false;
		}
	}

	public boolean isDrop() {
		switch (this) {
		case PREPARE_DROP:
		case BEFORE_DROP:
		case AFTER_DROP:
			return true;
		default:
			return false;
		}
	}

	public boolean isExists() {
		switch (this) {
		case PREPARE_EXISTS:
		case BEFORE_EXISTS:
		case AFTER_EXISTS:
			return true;
		default:
			return false;
		}
	}

}
