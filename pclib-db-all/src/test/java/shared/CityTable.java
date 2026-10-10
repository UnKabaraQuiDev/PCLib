package shared;

import java.util.function.Function;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.query.QueryFunction;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class CityTable extends DatabaseTable<CityData> {

	@QueryFunction
	private Function<Object[], Object> testOutgoingFk;

	public CityTable(final Database database) {
		super(database);
	}

	@Query
	public CityData testOutgoingFk(@Param GarageData garage) {
		return (CityData) testOutgoingFk.apply(new Object[] { garage });
	}

	@Query
	public CityData testOutOfRangeFk(@Param CarData garage) {
		return null;
	}

	@Query(tables = { @Table(typeName = GarageTable.class) })
	public CityData testInRangeFk(@Param CarData garage) {
		return null;
	}

	@Query(tables = { @Table(typeName = GarageTable.class), @Table(typeName = CarTable.class) })
	public CityData testInRangeFk2(@Param PersonData garage) {
		return null;
	}

}
