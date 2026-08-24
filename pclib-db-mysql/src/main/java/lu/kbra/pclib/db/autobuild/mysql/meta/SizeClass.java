package lu.kbra.pclib.db.autobuild.mysql.meta;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SizeClass {

	TINY("TINY"),
	SMALL("SMALL"),
	NORMAL(""),
	MEDIUM("MEDIUM"),
	LONG("LONG");

	private final String sql;

	public String asSql() {
		return sql;
	}

}
