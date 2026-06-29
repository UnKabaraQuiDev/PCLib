
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.autobuild.mysql.IntTypes;
import lu.kbra.pclib.db.connector.SQLiteDataBaseConnector;
import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultTableHints;

public class TableStructureTest {

	private static TableStructure structureStub(final String name) {
		return new TableStructure(name, new HashMap<>(0), new ColumnData[0], new ConstraintData[0]);
	}

	@Test
	public void buildIncludesColumnsConstraintsAndMysqlOptions() {
		final ColumnData id = new ColumnData(Optional.empty(), "id", new HashMap<>(0), new IntTypes.IntType(), true, false, null, null);
		final ConstraintData pk = new PrimaryKeyData("pk_people", new String[] { "id" });

		final TableStructure structure = new TableStructure("people", new HashMap<String, Object>() {
			{
				this.put(DefaultTableHints.CHARACTER_SET, MySQLDbmsProvider.DEFAULT_CHARACTER_SET);
				this.put(DefaultTableHints.ENGINE, MySQLDbmsProvider.DEFAULT_ENGINE);
			}
		}, new ColumnData[] { id }, new ConstraintData[] { pk });

		final String sql = Arrays.stream(SQLStructureVisitors.forProtocol("mysql").create(structure)).collect(Collectors.joining("\n"));
		System.out.println(sql);
		Assertions.assertTrue(sql.startsWith("CREATE TABLE `people` (\n"));
		Assertions.assertTrue(sql.contains("  `id` INT AUTO_INCREMENT NOT NULL"));
		Assertions.assertTrue(sql.contains("  CONSTRAINT `pk_people` PRIMARY KEY (`id`)"));
		Assertions.assertTrue(sql.endsWith(") CHARACTER SET utf8mb4 ENGINE=InnoDB;\n"));
	}

	@Test
	public void buildUsesSQLiteDialect() {
		final ColumnData id = new ColumnData(Optional.empty(), "id", new HashMap<>(0), new IntTypes.IntType(), true, false, null, null);
		final TableStructure structure = new TableStructure("people",
				new HashMap<>(0),
				new ColumnData[] { id },
				new ConstraintData[] { new PrimaryKeyData(TableStructureTest.structureStub("people"), new String[] { "id" }) });

		final SQLiteDataBaseConnector connector = new SQLiteDataBaseConnector(".");
		connector.setDatabase("test");

		final String sql = Arrays.stream(SQLStructureVisitors.forProtocol("sqlite").create(structure)).collect(Collectors.joining("\n"));
		System.out.println(sql);
		Assertions.assertTrue(sql.startsWith("CREATE TABLE \"people\" (\n"));
		Assertions.assertTrue(sql.contains("  \"id\" INTEGER PRIMARY KEY AUTOINCREMENT"));
		Assertions.assertFalse(sql.contains("AUTO_INCREMENT"));
		Assertions.assertFalse(sql.contains("CHARACTER SET"));
		Assertions.assertFalse(sql.contains("ENGINE="));
	}

}
