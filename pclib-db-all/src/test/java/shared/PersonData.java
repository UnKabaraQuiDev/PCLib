package shared;

import java.sql.Date;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.entry.Generated.Type;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.Version;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonData implements DatabaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column
	@Unique
	protected @MaxLength(30) String name;

	@Column
	protected Date birthDate;

	@Column
	@Generated(Type.STORED)
	@DefaultValue(value = "EXTRACT(YEAR FROM birth_date)::INTEGER", dbms = PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME)
	@DefaultValue(value = "YEAR(birth_date)", dbms = MySQLDbmsProvider.DBMS_QUALIFIER_NAME)
	@DefaultValue(value = "CAST(strftime('%Y', birth_date) AS INTEGER)", dbms = SQLiteDbmsProvider.DBMS_QUALIFIER_NAME)
	protected Integer birthYear;

	@Column
	@Version
	protected int version;

	public PersonData(final int id) {
		this.id = id;
	}

	public PersonData(final String name, final Date birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}

	@Override
	public PersonData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
