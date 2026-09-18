package shared;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class PersonTable extends DatabaseTable<PersonData> {

	public PersonTable(final Database database) {
		super(database);
	}

	@Query
	public PersonData testIncomingFk(@Param CarData garage) {
		return null;
	}

	@Query
	public PersonData testOutOfRangeFk(@Param GarageData garage) {
		return null;
	}

	@Query(tables = { @Table(typeName = CarTable.class) })
	public PersonData testInRangeFk(@Param GarageData garage) {
		return null;
	}

	@Query(tables = { @Table(typeName = CarTable.class), @Table(typeName = GarageTable.class) })
	public PersonData testInRangeFk2(@Param CityData garage) {
		return null;
	}

}
