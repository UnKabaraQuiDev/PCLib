package shared;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.query.QueryFunction;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.query.ParamFunction;
import lu.kbra.pclib.db.table.DatabaseTable;

public class CityTable extends DatabaseTable<CityData> {

	@QueryFunction
	private ParamFunction testOutgoingFk;

	public CityTable(final Database database) {
		super(database);
	}

	@Query
	public CityData testOutgoingFk(@Param final GarageData garage) {
		return this.testOutgoingFk.get(garage);
	}

	@Query
	public CityData testOutOfRangeFk(@Param final CarData garage) {
		return null;
	}

	@Query(tables = { @Table(typeName = GarageTable.class) })
	public CityData testInRangeFk(@Param final CarData garage) {
		return null;
	}

	@Query(tables = { @Table(typeName = GarageTable.class), @Table(typeName = CarTable.class) })
	public CityData testInRangeFk2(@Param final PersonData garage) {
		return null;
	}

}
