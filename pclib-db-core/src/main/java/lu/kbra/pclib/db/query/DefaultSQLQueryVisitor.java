package lu.kbra.pclib.db.query;

/**
 * Default SQL visitor used when no connector is available yet.
 *
 * <p>
 * Concrete DBMS modules provide their own visitors through {@code DbmsProvider}.
 * </p>
 */
public class DefaultSQLQueryVisitor extends AbstractSQLQueryVisitor {

	public DefaultSQLQueryVisitor() {
		super('`');
	}

}
