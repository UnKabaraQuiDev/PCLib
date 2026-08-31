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
								@SelectColumn(name = "id", asName = "{M:personId}"),
								@SelectColumn(name = "name", asName = "{M:personName}") }
				),
				@Table(
						typeName = CarTable.class,
						join = Table.Type.INNER,
						asName = "c",
						columns = {
								@SelectColumn(name = "id", asName = "{M:carId}"),
								@SelectColumn(name = "brand", asName = "{M:carBrand}") }
				) }
)
public class PersonCarView extends DatabaseView<PersonCarROData> {

	public PersonCarView(final Database database) {
		super(database);
	}

	public List<PersonCarROData> loadAll() {
		return new BufferedPagedEnumeration<>(20, this, "person_id").stream().collect(Collectors.toList());
	}

}
