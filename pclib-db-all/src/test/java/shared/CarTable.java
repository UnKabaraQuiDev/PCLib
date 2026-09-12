package shared;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class CarTable extends DatabaseTable<CarData> {

	public CarTable(final Database database) {
		super(database);
	}

	@Query
	public CarData testOutgoingFk(@Param PersonData person) {
		return null;
	}

	@Query
	public CarData testIncomingFk(@Param GarageData garage) {
		return null;
	}

}
