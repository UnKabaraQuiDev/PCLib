package lu.kbra.pclib.db.autobuild.view;

public class ViewOrderStructure {

	private String column;
	private String type;

	public ViewOrderStructure(String column, String type) {
		this.column = column;
		this.type = type;
	}

	public ViewOrderStructure() {
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ViewOrderStructure@" + System.identityHashCode(this) + " [column=" + column + ", type=" + type + "]";
	}

}