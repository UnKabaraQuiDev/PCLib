package lu.kbra.pclib.db.dbms;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.sqlite.column.text.JsonArrayColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.JsonObjectColumnType;
import lu.kbra.pclib.db.dbms.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;

public class SQLiteOrgJsonColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public boolean supports(String dbmsQualifierName) {
		return SQLiteDbmsProvider.DBMS_QUALIFIER_NAME.equalsIgnoreCase(dbmsQualifierName);
	}

	@Override
	public boolean shouldRegister() {
		return ColumnTypeRegistry.isClassPresent("org.json.JSONObject") && ColumnTypeRegistry.isClassPresent("org.json.JSONArray");
	}

	@Override
	public void registerColumnTypes(final List<ColumnTypeFactory<?>> typeMap) {
		ColumnTypeRegistry.registerType(JsonObjectColumnType.class,
				(clazz, map, etp) -> clazz == JSONObject.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new JsonObjectColumnType(),
				typeMap);

		ColumnTypeRegistry.registerType(JsonArrayColumnType.class,
				(clazz, map, etp) -> clazz == JSONArray.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new JsonArrayColumnType(),
				typeMap);
	}

}
