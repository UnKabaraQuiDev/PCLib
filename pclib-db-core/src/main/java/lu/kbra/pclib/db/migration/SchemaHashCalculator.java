package lu.kbra.pclib.db.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;

public class SchemaHashCalculator {

	public static String calculate(final DatabaseStructure structure) {
		if (structure == null) {
			throw new IllegalArgumentException("structure cannot be null");
		}

		final String canonical = SchemaHashCalculator.canonicalize(structure);

		return PCUtils.hashStringSha256(canonical);
	}

	private static String canonicalize(final DatabaseStructure structure) {
		final List<TableStructure> tables = new ArrayList<>(structure.getTableStructures());

		tables.sort(Comparator.comparing(SchemaHashCalculator::tableKey));

		final StringBuilder result = new StringBuilder();

		for (final TableStructure table : tables) {

			result.append("TABLE\n");
			result.append(SchemaHashCalculator.tableKey(table)).append('\n');

			SchemaHashCalculator.appendColumns(result, table);
			SchemaHashCalculator.appendConstraints(result, table);
		}

		return result.toString();
	}

	private static String tableKey(final TableStructure table) {
		return table.getQualifiedName();
	}

	private static void appendColumns(final StringBuilder result, final TableStructure table) {
		final List<ColumnData> columns = new ArrayList<>();

		if (table.getColumns() != null) {
			columns.addAll(Arrays.asList(table.getColumns()));
		}

		columns.sort(Comparator.comparing(ColumnData::getLocalName));

		for (final ColumnData column : columns) {
			result.append("COLUMN\n");
			result.append(column.getLocalName()).append('\n');
			result.append(column.getLocalQualifiedName()).append('\n');
			result.append(SchemaHashCalculator.typeSignature(column)).append('\n');
			result.append(column.isNullable()).append('\n');
			result.append(column.isPrimaryKey()).append('\n');
			result.append(column.isUnique()).append('\n');
			result.append(column.isAutoIncrement()).append('\n');

			SchemaHashCalculator.appendDefaultValue(result, column);

			result.append('\n');
		}
	}

	private static String typeSignature(final ColumnData column) {
		return column.getType().getEncodingType().getTypeName();
	}

	private static void appendDefaultValue(final StringBuilder result, final ColumnData column) {

		final Object value = column.getHint(DefaultColumnHints.DEFAULT_VALUE);

		result.append(value == null ? "" : String.valueOf(value));
	}

	private static void appendConstraints(final StringBuilder result, final TableStructure table) {

		final ConstraintData[] constraints = table.getConstraints();

		if (constraints == null) {
			return;
		}

		final List<ConstraintData> sorted = new ArrayList<>(Arrays.asList(constraints));

		sorted.sort(Comparator.comparing(SchemaHashCalculator::constraintKey));

		for (final ConstraintData constraint : sorted) {

			result.append("CONSTRAINT\n");

			result.append(SchemaHashCalculator.constraintKey(constraint)).append('\n');

			result.append(SchemaHashCalculator.serializeConstraint(constraint)).append('\n');
		}
	}

	private static String constraintKey(final ConstraintData constraint) {
		return constraint.getClass().getName() + ":" + String.valueOf(constraint.getName());
	}

	private static String serializeConstraint(final ConstraintData constraint) {
		if (constraint instanceof PrimaryKeyData) {
			return MigrationSupport.serializePrimaryKey((PrimaryKeyData) constraint);
		}
		if (constraint instanceof UniqueData) {
			return MigrationSupport.serializeUnique((UniqueData) constraint);
		}
		if (constraint instanceof ForeignKeyData) {
			return MigrationSupport.serializeForeignKey((ForeignKeyData) constraint);
		}
		if (constraint instanceof CheckData) {
			return MigrationSupport.serializeCheck((CheckData) constraint);
		}

		throw new IllegalArgumentException("Unsupported constraint type: " + constraint.getClass().getName());
	}

}
