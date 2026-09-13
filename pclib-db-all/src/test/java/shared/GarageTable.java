package shared;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class GarageTable extends DatabaseTable<GarageData> {

	public GarageTable(final Database database) {
		super(database);
	}

	@Query
	public GarageData testOutgoingFk(@Param CarData car) {
		return null;
	}

	@Query
	public GarageData testIncomingFk(@Param CityData city) {
		return null;
	}

}
