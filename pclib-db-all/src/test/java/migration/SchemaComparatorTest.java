package migration;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.migration.compare.ColumnAdded;
import lu.kbra.pclib.db.migration.compare.ColumnNullableChanged;
import lu.kbra.pclib.db.migration.compare.ColumnRemoved;
import lu.kbra.pclib.db.migration.compare.ColumnRenamed;
import lu.kbra.pclib.db.migration.compare.ColumnTypeChanged;
import lu.kbra.pclib.db.migration.compare.SchemaComparator;
import lu.kbra.pclib.db.migration.compare.SchemaDelta;
import lu.kbra.pclib.db.utils.BaseDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import utils.DummyTable;

public class SchemaComparatorTest {

	private final DatabaseEntryUtils dbEntryUtils = new BaseDatabaseEntryUtils("mysql");

	@Test
	public void testCompare1_2() {
		final TableStructure struct1 = new DummyTable<>(dbEntryUtils, AxaData1.class).scan();
		final TableStructure struct2 = new DummyTable<>(dbEntryUtils, AxaData2.class).scan();

		final SchemaDelta delta = SchemaComparator.compare(struct1, struct2);

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(c -> c instanceof ColumnAdded);
		assert delta.getChanges()
				.stream()
				.filter(c -> c instanceof ColumnAdded)
				.map(ColumnAdded.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "country".equals(c.getColumn().getLocalName()))
				.get();
	}

	@Test
	public void testCompare2_3() {
		final TableStructure struct1 = new DummyTable<>(dbEntryUtils, AxaData2.class).scan();
		final TableStructure struct2 = new DummyTable<>(dbEntryUtils, AxaData3.class).scan();

		final SchemaDelta delta = SchemaComparator.compare(struct1, struct2);

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(c -> c instanceof ColumnRemoved);
		assert delta.getChanges()
				.stream()
				.filter(c -> c instanceof ColumnRemoved)
				.map(ColumnRemoved.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "country".equals(c.getColumn().getLocalName()))
				.get();
	}

	@Test
	public void testCompare3_4() {
		final TableStructure struct1 = new DummyTable<>(dbEntryUtils, AxaData3.class).scan();
		final TableStructure struct2 = new DummyTable<>(dbEntryUtils, AxaData4.class).scan();

		final SchemaDelta delta = SchemaComparator.compare(struct1, struct2);

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(c -> c instanceof ColumnRenamed);
		assert delta.getChanges()
				.stream()
				.filter(c -> c instanceof ColumnRenamed)
				.map(ColumnRenamed.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "name".equals(c.getOldColumn().getLocalName()) && "city_name".equals(c.getNewColumn().getLocalName()))
				.get();
	}

	@Test
	public void testCompare4_5() {
		final TableStructure struct1 = new DummyTable<>(dbEntryUtils, AxaData4.class).scan();
		final TableStructure struct2 = new DummyTable<>(dbEntryUtils, AxaData5.class).scan();

		final SchemaDelta delta = SchemaComparator.compare(struct1, struct2);

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(c -> c instanceof ColumnTypeChanged);
		assert delta.getChanges()
				.stream()
				.filter(c -> c instanceof ColumnTypeChanged)
				.map(ColumnTypeChanged.class::cast)
				.peek(System.out::println)
				.peek(c -> System.out.println(c.getNewColumn().getType().getEncodingType().build()))
				.peek(c -> System.out.println(c.getOldColumn().getType().getEncodingType().build()))
				.findFirst()
				.map(c -> "postal_code".equals(c.getNewColumn().getLocalName())
						&& "INT".equals(c.getNewColumn().getType().getEncodingType().build())
						&& "VARCHAR(10)".equals(c.getOldColumn().getType().getEncodingType().build()))
				.get();
	}

	@Test
	public void testCompare5_6() {
		final TableStructure struct1 = new DummyTable<>(dbEntryUtils, AxaData5.class).scan();
		final TableStructure struct2 = new DummyTable<>(dbEntryUtils, AxaData6.class).scan();

		final SchemaDelta delta = SchemaComparator.compare(struct1, struct2);

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(c -> c instanceof ColumnNullableChanged);
		assert delta.getChanges()
				.stream()
				.filter(c -> c instanceof ColumnNullableChanged)
				.map(ColumnNullableChanged.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "city_name".equals(c.getNewColumn().getLocalName()) && !c.getOldColumn().isNullable()
						&& c.getNewColumn().isNullable())
				.get();
	}

}
