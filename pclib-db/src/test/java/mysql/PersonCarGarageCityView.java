package mysql;

import java.util.List;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.ViewColumn;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.loader.BufferedPagedEnumeration;
import lu.kbra.pclib.db.view.DataBaseView;

@DB_View(
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
public class PersonCarGarageCityView extends DataBaseView<PersonCarGarageCityROData> {

	public PersonCarGarageCityView(DataBase dataBase) {
		super(dataBase);
	}

	public List<PersonCarGarageCityROData> loadAll() {
		return new BufferedPagedEnumeration<>(20, this, "person_id").stream().collect(Collectors.toList());
	}
}