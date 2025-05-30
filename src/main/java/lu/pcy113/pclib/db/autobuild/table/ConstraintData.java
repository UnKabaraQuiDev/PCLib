package lu.pcy113.pclib.db.autobuild.table;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.autobuild.SQLBuildable;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

public abstract class ConstraintData implements SQLBuildable {

	public abstract String getName();

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(getName());
	}

	public static ConstraintData from(TableStructure ts, Constraint ca, DataBaseEntryUtils dbEntryUtils) {
		switch (ca.type()) {
		case CHECK:
			break;
		case FOREIGN_KEY:
			return new ForeignKeyData(ts, ca.columns(), ca.referenceTable().isEmpty() ? dbEntryUtils.getQueryableName(ca.referenceTableType()) : ca.referenceTable(), new String[] { ca.referenceColumn() });
		case INDEX:
			PCUtils.notImplemented();
			// return new IndexData(ts, ca.name(), ca.columns());
		case PRIMARY_KEY:
			return new PrimaryKeyData(ts, ca.name(), ca.columns());
		case UNIQUE:
			return new UniqueData(ts, ca.name(), ca.columns());
		default:
			throw new IllegalArgumentException("Unknown constraint type: " + ca.type());
		}

		return null;
	}

}
