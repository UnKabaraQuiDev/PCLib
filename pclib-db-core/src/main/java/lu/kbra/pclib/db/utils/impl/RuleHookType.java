package lu.kbra.pclib.db.utils.impl;

public enum RuleHookType {

	BEFORE_INSERT,
	DURING_INSERT,
	AFTER_INSERT,

	BEFORE_LOAD,
	DURING_LOAD,
	AFTER_LOAD,

	BEFORE_UPDATE,
	DURING_UPDATE,
	AFTER_UPDATE,

	BEFORE_QUERY,
	DURING_QUERY,
	AFTER_QUERY,

	BEFORE_TRUNCATE,
	AFTER_TRUNCATE,

	BEFORE_CLEAR,
	AFTER_CLEAR,

	BEFORE_COUNT,
	AFTER_COUNT,

	BEFORE_CREATE,
	DURING_CREATE,
	AFTER_CREATE,

	BEFORE_DELETE,
	AFTER_DELETE,

	BEFORE_DROP,
	AFTER_DROP,

	BEFORE_EXISTS,
	AFTER_EXISTS;

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
		case DURING_UPDATE:
		case DURING_QUERY:
		case DURING_CREATE:
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
		case BEFORE_UPDATE:
		case DURING_UPDATE:
		case AFTER_UPDATE:
			return true;
		default:
			return false;
		}
	}

	public boolean isQuery() {
		switch (this) {
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
		case BEFORE_TRUNCATE:
		case AFTER_TRUNCATE:
			return true;
		default:
			return false;
		}
	}

	public boolean isClear() {
		switch (this) {
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
			return true;
		default:
			return false;
		}
	}

	public boolean isCreate() {
		switch (this) {
		case BEFORE_CREATE:
		case DURING_CREATE:
		case AFTER_CREATE:
			return true;
		default:
			return false;
		}
	}

	public boolean isDelete() {
		switch (this) {
		case BEFORE_DELETE:
		case AFTER_DELETE:
			return true;
		default:
			return false;
		}
	}

	public boolean isDrop() {
		switch (this) {
		case BEFORE_DROP:
		case AFTER_DROP:
			return true;
		default:
			return false;
		}
	}

	public boolean isExists() {
		switch (this) {
		case BEFORE_EXISTS:
		case AFTER_EXISTS:
			return true;
		default:
			return false;
		}
	}

}
