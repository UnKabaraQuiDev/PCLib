package test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.migration.MigrationOption;
import lu.kbra.pclib.db.migration.compare.ColumnAdded;
import lu.kbra.pclib.db.migration.compare.ColumnNullableChanged;
import lu.kbra.pclib.db.migration.compare.ColumnRemoved;
import lu.kbra.pclib.db.migration.compare.ColumnRenamed;
import lu.kbra.pclib.db.migration.compare.ColumnTypeChanged;
import lu.kbra.pclib.db.migration.compare.SchemaComparator;
import lu.kbra.pclib.db.migration.compare.SchemaDelta;

import shared.migration.schema.data.AxaData1;
import shared.migration.schema.data.AxaData2;
import shared.migration.schema.data.AxaData3;
import shared.migration.schema.data.AxaData4;
import shared.migration.schema.data.AxaData5;
import shared.migration.schema.data.AxaData6;
import shared.migration.schema.table.AxaTableN;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public interface DBSchemaComparatorTest extends GenericDBTest {

	@Test
	@Order(10)
	default void testCompare1_2() {
		final Database db = this.getDatabase();
		db.drop();
		assert !db.exists();

		final AxaTableN<AxaData1> table1 = new AxaTableN<>(db, AxaData1.class);
		db.clearBeans().initMigrationSupport(true).register(table1).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));
		final AxaTableN<AxaData2> table2 = new AxaTableN<>(db, AxaData2.class);
		db.clearBeans().initMigrationSupport(true).register(table2).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));

		final SchemaDelta delta = SchemaComparator.compare(table1.getStructure(), table2.getStructure());

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(ColumnAdded.class::isInstance);
		assert delta.getChanges()
				.stream()
				.filter(ColumnAdded.class::isInstance)
				.map(ColumnAdded.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "country".equals(c.getColumn().getLocalName()))
				.get();
	}

	@Test
	@Order(11)
	default void testCompare2_3() {
		final Database db = this.getDatabase();

		final AxaTableN<AxaData2> table1 = new AxaTableN<>(db, AxaData2.class);
		db.clearBeans().initMigrationSupport(true).register(table1).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));
		final AxaTableN<AxaData3> table2 = new AxaTableN<>(db, AxaData3.class);
		db.clearBeans().initMigrationSupport(true).register(table2).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));

		final SchemaDelta delta = SchemaComparator.compare(table1.getStructure(), table2.getStructure());

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(ColumnRemoved.class::isInstance);
		assert delta.getChanges()
				.stream()
				.filter(ColumnRemoved.class::isInstance)
				.map(ColumnRemoved.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "country".equals(c.getColumn().getLocalName()))
				.get();
	}

	@Test
	@Order(12)
	default void testCompare3_4() {
		final Database db = this.getDatabase();

		final AxaTableN<AxaData3> table1 = new AxaTableN<>(db, AxaData3.class);
		db.clearBeans().initMigrationSupport(true).register(table1).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));
		final AxaTableN<AxaData4> table2 = new AxaTableN<>(db, AxaData4.class);
		db.clearBeans().initMigrationSupport(true).register(table2).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));

		final SchemaDelta delta = SchemaComparator.compare(table1.getStructure(), table2.getStructure());

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(ColumnRenamed.class::isInstance);
		assert delta.getChanges()
				.stream()
				.filter(ColumnRenamed.class::isInstance)
				.map(ColumnRenamed.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "name".equals(c.getOldColumn().getLocalName()) && "city_name".equals(c.getNewColumn().getLocalName()))
				.get();
	}

	@Test
	@Order(13)
	default void testCompare4_5() {
		final Database db = this.getDatabase();

		final AxaTableN<AxaData4> table1 = new AxaTableN<>(db, AxaData4.class);
		db.clearBeans().initMigrationSupport(true).register(table1).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));
		final AxaTableN<AxaData5> table2 = new AxaTableN<>(db, AxaData5.class);
		db.clearBeans().initMigrationSupport(true).register(table2).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));

		final SchemaDelta delta = SchemaComparator.compare(table1.getStructure(), table2.getStructure());

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(ColumnTypeChanged.class::isInstance);
		assert delta.getChanges()
				.stream()
				.filter(c -> c instanceof ColumnTypeChanged)
				.map(ColumnTypeChanged.class::cast)
				.peek(System.out::println)
				.peek(c -> System.out.println(c.getNewColumn().getType().getEncodingType().build()))
				.peek(c -> System.out.println(c.getOldColumn().getType().getEncodingType().build()))
				.findFirst()
				.map(c -> "postal_code".equals(c.getNewColumn().getLocalName()) && !Objects
						.equals(c.getNewColumn().getType().getEncodingType().build(), c.getOldColumn().getType().getEncodingType().build()))
				.get();
	}

	@Test
	@Order(14)
	default void testCompare5_6() {
		final Database db = this.getDatabase();

		final AxaTableN<AxaData5> table1 = new AxaTableN<>(db, AxaData5.class);
		db.clearBeans().initMigrationSupport(true).register(table1).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));
		final AxaTableN<AxaData6> table2 = new AxaTableN<>(db, AxaData6.class);
		db.clearBeans().initMigrationSupport(true).register(table2).scanFromBeans().migrate(Collections.EMPTY_LIST, (t, b) -> {
		}, EnumSet.allOf(MigrationOption.class));

		final SchemaDelta delta = SchemaComparator.compare(table1.getStructure(), table2.getStructure());

		assert delta.hasChanges();
		assert delta.getChanges().stream().anyMatch(ColumnNullableChanged.class::isInstance);
		assert delta.getChanges()
				.stream()
				.filter(ColumnNullableChanged.class::isInstance)
				.map(ColumnNullableChanged.class::cast)
				.peek(System.out::println)
				.findFirst()
				.map(c -> "city_name".equals(c.getNewColumn().getLocalName()) && !c.getOldColumn().isNullable()
						&& c.getNewColumn().isNullable())
				.get();
	}

	@Test
	@Order(20)
	default void testCompare_cleanup() {
		getDatabase().drop();
		assert !getDatabase().exists();
	}

}
