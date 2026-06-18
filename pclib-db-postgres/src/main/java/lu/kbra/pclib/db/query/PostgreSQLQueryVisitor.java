package lu.kbra.pclib.db.query;

public class PostgreSQLQueryVisitor extends AbstractSQLQueryVisitor {

	public PostgreSQLQueryVisitor() {
		super('"');
	}

}
