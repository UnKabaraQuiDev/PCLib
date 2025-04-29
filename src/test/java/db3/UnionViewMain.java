package db3;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.DataBaseView;
import lu.pcy113.pclib.db.annotations.base.DB_Base;
import lu.pcy113.pclib.db.annotations.view.DB_View;
import lu.pcy113.pclib.db.annotations.view.UnionTable;
import lu.pcy113.pclib.db.annotations.view.ViewColumn;
import lu.pcy113.pclib.db.annotations.view.ViewTable;
import lu.pcy113.pclib.db.impl.SQLEntry;

public class UnionViewMain {

	public class UnionData implements SQLEntry {

		public UnionData clone() {
			return new UnionData();
		}

	}

	//@formatter:off
	@DB_View(name = "union_view", tables = {
			@ViewTable(name = "union_view_main", join = ViewTable.Type.MAIN_UNION, columns = {
					@ViewColumn(name = "id"),
					@ViewColumn(func = "SUM(points)")
			})
	},
	unionTables = {
			@UnionTable(name = "from_union_view", columns = {
					@ViewColumn(name = "id"),
					@ViewColumn(func = "points")
			}),
			@UnionTable(name = "from_union_view_2", columns = {
					@ViewColumn(name = "id"),
					@ViewColumn(func = "points")
			})
	})
	//@formatter:on
	public static final class UnionView extends DataBaseView<UnionData> {

		public UnionView(DataBase db) {
			super(db);
		}

	}
	
	//@formatter:off
	@DB_Base(name = "union_db")
	//@formatter:on
	public static final class UnionDB extends DataBase {

		public UnionDB(DataBaseConnector connector) {
			super(connector);
		}
		
	}

	@Test
	public void test() {
		System.out.println(new UnionView(new UnionDB(new DataBaseConnector())).getCreateSQL());
	}

}
