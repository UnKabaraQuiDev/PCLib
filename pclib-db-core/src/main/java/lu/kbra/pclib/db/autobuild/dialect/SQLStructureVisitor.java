package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.DataBaseStructure;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.autobuild.view.ViewStructure;

public interface SQLStructureVisitor {

	String drop(DataBaseStructure dataBaseStructure);

	String drop(TableStructure tableStructure);

	String drop(ViewStructure tableStructure);

	String visit(DataBaseStructure db);

	String visit(TableStructure table);

	String visit(TableStructure table, ColumnData column);

	String visit(ViewStructure view);

}
