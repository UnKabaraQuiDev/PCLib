package shared.migration.third.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.third.data.AddressData3;

public class AddressTable3 extends DatabaseTable<AddressData3> {

	public AddressTable3(Database database) {
		super(database, "address");
	}

}
