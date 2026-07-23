package shared;

import java.util.List;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.annotations.view.DBView;
import lu.kbra.pclib.db.annotations.view.ViewColumn;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.loader.BufferedPagedEnumeration;
import lu.kbra.pclib.db.view.DatabaseView;

@DBView(
		name = "person_car_garage_city_view",
		tables = {
				@ViewTable(
						typeName = PersonTable.class,
						asName = "p",
						columns = { @ViewColumn(name = "id", asName = "person_id"), @ViewColumn(name = "name", asName = "person_name") }
				),
				@ViewTable(
						typeName = CarTable.class,
						join = ViewTable.Type.INNER,
						asName = "c",
						columns = { @ViewColumn(name = "brand", asName = "car_brand") }
				),
				@ViewTable(
						typeName = GarageTable.class,
						join = ViewTable.Type.INNER,
						asName = "g",
						columns = { @ViewColumn(name = "name", asName = "garage_name") }
				),
				@ViewTable(
						typeName = CityTable.class,
						join = ViewTable.Type.INNER,
						asName = "ci",
						columns = { @ViewColumn(name = "name", asName = "city_name") }
				) }
)
public class PersonCarGarageCityView extends DatabaseView<PersonCarGarageCityROData> {

	public PersonCarGarageCityView(final Database database) {
		super(database);
	}

	public List<PersonCarGarageCityROData> loadAll() {
		return new BufferedPagedEnumeration<>(20, this, "person_id").stream().collect(Collectors.toList());
	}
}
