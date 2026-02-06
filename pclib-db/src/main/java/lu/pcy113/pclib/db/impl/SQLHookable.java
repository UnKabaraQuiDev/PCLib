package lu.pcy113.pclib.db.impl;

import lu.pcy113.pclib.db.SQLRequestType;

public interface SQLHookable {

	void requestHook(SQLRequestType type, Object query);

}
