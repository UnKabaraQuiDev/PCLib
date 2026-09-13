package test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.query.QueryStructure;
import lu.kbra.pclib.db.exception.MissingDependencyException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.PrimaryKeyColumnType;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
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
		db.create();
		personTable.create();
		carTable.create();
		garageTable.create();
		cityTable.create();

		final ProxyDatabaseEntryUtils dbEntryUtils = (ProxyDatabaseEntryUtils) db.getDatabaseEntryUtils();
		final QueryFunctionProvider functionProvider = dbEntryUtils.getQueryFunctionProvider();
		final SQLStructureVisitor structureVisitor = dbEntryUtils.getStructureVisitor();

		this.assertContains(this.getStructure(functionProvider, cityTable, "testOutgoingFk").getSql(), "garage_id");
		GenericDBTest.assertThrowsInCauses(MissingDependencyException.class,
				() -> this.getStructure(functionProvider, cityTable, "testOutOfRangeFk").getSql());
		this.assertContains(this.getStructure(functionProvider, cityTable, "testInRangeFk").getSql(),
				structureVisitor.qualifiedName("garage", "id"));
		this.assertContains(this.getStructure(functionProvider, cityTable, "testInRangeFk2").getSql(),
				structureVisitor.qualifiedName("car", "id"));

		this.assertContains(this.getStructure(functionProvider, garageTable, "testOutgoingFk").getSql(), "car_id");
		this.assertContains(this.getStructure(functionProvider, garageTable, "testIncomingFk").getSql(),
				structureVisitor.qualifiedName("garage", "id"));

		this.assertContains(this.getStructure(functionProvider, carTable, "testOutgoingFk").getSql(), "person_id");
		this.assertContains(this.getStructure(functionProvider, carTable, "testIncomingFk").getSql(),
				structureVisitor.qualifiedName("car", "id"));

		this.assertContains(this.getStructure(functionProvider, personTable, "testIncomingFk").getSql(),
				structureVisitor.qualifiedName("person", "id"));
		GenericDBTest.assertThrowsInCauses(MissingDependencyException.class,
				() -> this.getStructure(functionProvider, personTable, "testOutOfRangeFk").getSql());
		this.assertContains(this.getStructure(functionProvider, personTable, "testInRangeFk").getSql(),
				structureVisitor.qualifiedName("car", "id"));
		this.assertContains(this.getStructure(functionProvider, personTable, "testInRangeFk2").getSql(),
				structureVisitor.qualifiedName("garage", "id"));

		this.assertParamPk(dbEntryUtils, functionProvider, cityTable, "testOutgoingFk", garageTable);
		this.assertParamPk(dbEntryUtils, functionProvider, cityTable, "testInRangeFk", carTable);
		this.assertParamPk(dbEntryUtils, functionProvider, cityTable, "testInRangeFk2", personTable);

		this.assertParamPk(dbEntryUtils, functionProvider, garageTable, "testOutgoingFk", carTable);
		this.assertParamPk(dbEntryUtils, functionProvider, garageTable, "testIncomingFk", "city", "garage_id");

		this.assertParamPk(dbEntryUtils, functionProvider, carTable, "testOutgoingFk", personTable);
		this.assertParamPk(dbEntryUtils, functionProvider, carTable, "testIncomingFk", "garage", "car_id");

		this.assertParamPk(dbEntryUtils, functionProvider, personTable, "testIncomingFk", "car", "person_id");
		this.assertParamPk(dbEntryUtils, functionProvider, personTable, "testInRangeFk", "garage", "car_id");
		this.assertParamPk(dbEntryUtils, functionProvider, personTable, "testInRangeFk2", "city", "garage_id");

//		final CaptureRule captureRule = new CaptureRule();
//		db.getDatabaseEntryUtils().getQueryableHookManager().add(captureRule).computeCache();
//
//		this.getFunction(functionProvider, cityTable, "testOutgoingFk").apply(new Object[] { new GarageData(0, 1, "name") });
//		System.out.println(captureRule.getLast());

		cityTable.drop();
		Assert.assertTrue(!cityTable.exists());
		garageTable.drop();
		Assert.assertTrue(!garageTable.exists());
		carTable.drop();
		Assert.assertTrue(!carTable.exists());
		personTable.drop();
		Assert.assertTrue(!personTable.exists());
	}

	default void assertParamPk(
			final DatabaseEntryUtils dbEntryUtils,
			final QueryFunctionProvider functionProvider,
			final SQLQueryable<?> targetTable,
			final String methodName,
			final SQLQueryable<?> paramTable) {
		final PrimaryKeyColumnType<?> type = (PrimaryKeyColumnType<?>) this.getStructure(functionProvider, targetTable, methodName)
				.getParameters()[0].getType();
		for (int i = 0; i < type.getKeyColumns().length; i++) {
			System.out.println(type.getKeyColumns()[i].getQualifiedName());
			Assert.assertEquals(dbEntryUtils.getPrimaryKeys(paramTable)[i].getQualifiedName(), type.getKeyColumns()[i].getQualifiedName());
		}
	}

	default void assertParamPk(
			final DatabaseEntryUtils dbEntryUtils,
			final QueryFunctionProvider functionProvider,
			final SQLQueryable<?> targetTable,
			final String methodName,
			final String... columns) {
		final PrimaryKeyColumnType<?> type = (PrimaryKeyColumnType<?>) this.getStructure(functionProvider, targetTable, methodName)
				.getParameters()[0].getType();
		for (int i = 0; i < type.getKeyColumns().length; i++) {
			System.out.println(type.getKeyColumns()[i].getQualifiedName());
			Assert.assertTrue(
					type.getKeyColumns()[i].getQualifiedName().contains(dbEntryUtils.getStructureVisitor().qualifiedName(columns)));
		}
	}

	default void assertContains(final String sql, final String... string) {
		System.out.println(sql);
		Assert.assertTrue(Arrays.stream(string).allMatch(sql::contains));
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
