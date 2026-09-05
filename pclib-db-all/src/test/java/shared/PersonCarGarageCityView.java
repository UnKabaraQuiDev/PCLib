package shared;

import java.util.List;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.annotations.view.SelectColumn;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.annotations.view.View;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.loader.BufferedPagedEnumeration;
import lu.kbra.pclib.db.view.DatabaseView;

@View(
		tables = {
				@Table(
						typeName = PersonTable.class,
						asName = "p",
						columns = {
								@SelectColumn(name = "{M:id}", asName = "person_id"),
								@SelectColumn(name = "{M:name}", asName = "{M:personName}") }
				),
				@Table(
						typeName = CarTable.class,
						join = Table.Type.INNER,
						asName = "c",
						columns = { @SelectColumn(name = "{M:brand}", asName = "{M:carBrand}") }
				),
				@Table(
						typeName = GarageTable.class,
						join = Table.Type.INNER,
						asName = "g",
						columns = { @SelectColumn(name = "{M:name}", asName = "{M:garageName}") }
				),
				@Table(
						typeName = CityTable.class,
						join = Table.Type.INNER,
						asName = "ci",
						columns = { @SelectColumn(name = "{M:name}", asName = "{M:cityName}") }
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
