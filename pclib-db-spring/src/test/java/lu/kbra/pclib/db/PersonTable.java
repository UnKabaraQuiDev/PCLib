package lu.kbra.pclib.db;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.table.DeferredDataBaseTable;

@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

}
