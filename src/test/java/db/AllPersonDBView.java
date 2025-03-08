package db;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseView;
import lu.pcy113.pclib.db.annotations.DB_View;
import lu.pcy113.pclib.db.annotations.ViewColumn;
import lu.pcy113.pclib.db.annotations.ViewTable;

//@formatter:off
@DB_View(name = "all_persons",
	tables = {
		@ViewTable(name = "test", columns = {
			@ViewColumn(name = "name"),
			@ViewColumn(name = "date")
		})
	}
)
//@formatter:on
public class AllPersonDBView extends DataBaseView<Person> {

	public AllPersonDBView(DataBase dbTest) {
		super(dbTest);
	}
	
}
