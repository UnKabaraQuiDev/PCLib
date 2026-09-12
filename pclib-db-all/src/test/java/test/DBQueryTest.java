package test;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.query.QueryStructure;
import lu.kbra.pclib.db.exception.MissingDependencyException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.ProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.QueryFunctionProvider;

import shared.CarTable;
import shared.CityTable;
import shared.GarageTable;
import shared.PersonTable;

public interface DBQueryTest extends GenericDBTest {

	@Test
	default void testQueries() throws SQLException {
		final Database db = this.getDatabase();
		final CityTable cityTable = new CityTable(db);
		final GarageTable garageTable = new GarageTable(db);
		final CarTable carTable = new CarTable(db);
		final PersonTable personTable = new PersonTable(db);
		db.clearBeans().register(cityTable, garageTable, carTable, personTable).scanFromBeans();

		final ProxyDatabaseEntryUtils dbEntryUtils = (ProxyDatabaseEntryUtils) db.getDatabaseEntryUtils();
		final QueryFunctionProvider functionProvider = dbEntryUtils.getQueryFunctionProvider();
		final SQLStructureVisitor structureVisitor = dbEntryUtils.getStructureVisitor();

		assertContains(this.getStructure(functionProvider, cityTable, "testOutgoingFk").getSql(), "garage_id");
		GenericDBTest.assertThrowsInCauses(MissingDependencyException.class,
				() -> this.getStructure(functionProvider, cityTable, "testOutOfRangeFk").getSql());
		assertContains(this.getStructure(functionProvider, cityTable, "testInRangeFk").getSql(),
				structureVisitor.qualifiedName("garage", "id"));
		assertContains(this.getStructure(functionProvider, cityTable, "testInRangeFk2").getSql(),
				structureVisitor.qualifiedName("car", "id"));

		assertContains(this.getStructure(functionProvider, garageTable, "testOutgoingFk").getSql(), "car_id");
		assertContains(this.getStructure(functionProvider, garageTable, "testIncomingFk").getSql(),
				structureVisitor.qualifiedName("garage", "id"));

		assertContains(this.getStructure(functionProvider, carTable, "testOutgoingFk").getSql(), "person_id");
		assertContains(this.getStructure(functionProvider, carTable, "testIncomingFk").getSql(),
				structureVisitor.qualifiedName("car", "id"));

		assertContains(this.getStructure(functionProvider, personTable, "testIncomingFk").getSql(),
				structureVisitor.qualifiedName("person", "id"));
		GenericDBTest.assertThrowsInCauses(MissingDependencyException.class,
				() -> this.getStructure(functionProvider, personTable, "testOutOfRangeFk").getSql());
		assertContains(this.getStructure(functionProvider, personTable, "testInRangeFk").getSql(),
				structureVisitor.qualifiedName("car", "id"));
		assertContains(this.getStructure(functionProvider, personTable, "testInRangeFk2").getSql(),
				structureVisitor.qualifiedName("garage", "id"));
	}

	default void assertContains(String sql, String... string) {
		System.out.println(sql);
		assertTrue(Arrays.stream(string).allMatch(sql::contains));
	}

	default Function<Object[], Object>
			getFunction(final QueryFunctionProvider functionProvider, final SQLQueryable<?> instance, final String methodName) {
		return functionProvider.buildMethodQueryFunction(instance,
				Arrays.stream(instance.getClass().getDeclaredMethods())
						.filter(c -> c.getName().equals(methodName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Method: " + methodName + " not found.")));
	}

	default QueryStructure
			getStructure(final QueryFunctionProvider functionProvider, final SQLQueryable<?> instance, final String methodName) {
		return functionProvider.buildMethodQueryStructure(instance,
				new HashMap<>(),
				Arrays.stream(instance.getClass().getDeclaredMethods())
						.filter(c -> c.getName().equals(methodName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Method: " + methodName + " not found.")));
	}

}
