package lu.kbra.pclib.db;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;

@Component
public abstract class NTUserTable extends DeferredNTDataBaseTable<UserData> {

}
