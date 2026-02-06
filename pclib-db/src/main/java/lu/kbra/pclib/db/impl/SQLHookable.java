package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.SQLRequestType;

public interface SQLHookable {

	void requestHook(SQLRequestType type, Object query);

}
