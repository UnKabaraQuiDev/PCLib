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
		name = "person_car_view",
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
						on = "p.id = c.person_id",
						columns = { @ViewColumn(name = "id", asName = "car_id"), @ViewColumn(name = "brand", asName = "car_brand") }
				) }
)
public class PersonCarView extends DataBaseView<PersonCarViewData> {

	public PersonCarView(DataBase dataBase) {
		super(dataBase);
	}

	public List<PersonCarViewData> loadAll() {
		return new BufferedPagedEnumeration<>(20, this, "person_id").stream().collect(Collectors.toList());
	}

}