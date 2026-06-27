package lu.kbra.pclib.db.domain.view;

public class ViewOrderStructure {

	private String column;
	private String type;

	public ViewOrderStructure() {
	}

	public ViewOrderStructure(final String column, final String type) {
		this.column = column;
		this.type = type;
	}

	public String getColumn() {
		return this.column;
	}

	public String getType() {
		return this.type;
	}

	public void setColumn(final String column) {
		this.column = column;
	}

	public void setType(final String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ViewOrderStructure@" + System.identityHashCode(this) + " [column=" + this.column + ", type=" + this.type + "]";
	}

}
