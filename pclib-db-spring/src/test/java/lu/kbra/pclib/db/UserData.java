package lu.kbra.pclib.db;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class UserData implements DataBaseEntry {

	private static final long serialVersionUID = -5849931165499250644L;

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column
	@Unique
	protected @MaxLength(35) String name;

	@Column
	protected String pass;

	public UserData() {
	}

	public UserData(final long id) {
		this.id = id;
	}

	public UserData(final long id, final String name, final String pass) {
		this.id = id;
		this.name = name;
		this.pass = pass;
	}

	public UserData(final String name) {
		this.name = name;
	}

	public UserData(final String name, final String pass) {
		this.name = name;
		this.pass = pass;
	}

	@Override
	public String toString() {
		return "UserData@" + System.identityHashCode(this) + " [id=" + this.id + ", name=" + this.name + ", pass=" + this.pass + "]";
	}

}
