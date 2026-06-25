package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.autobuild.view.ViewStructure;

public interface SQLStructureVisitor {

	String visit(TableStructure table);

	String visit(ViewStructure view);

	String visitColumn(TableStructure table, ColumnData column);

}
