package lu.kbra.pclib.db.domain.view;

public class ViewColumnStructure {

	private String name;
	private String alias;
	private String func;

//	public ViewColumnStructure(String name) {
//		this.name = name;
//	}
//
//	public ViewColumnStructure(String name, String alias) {
//		this.name = name;
//		this.alias = alias;
//	}

	public ViewColumnStructure() {
	}

	public ViewColumnStructure(final String name, final String alias, final String func) {
		this.name = name;
		this.alias = alias;
		this.func = func;
	}

	public String getAlias() {
		return this.alias;
	}

	public String getFunc() {
		return this.func;
	}

	public String getName() {
		return this.name;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public void setFunc(final String func) {
		this.func = func;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ViewColumnStructure@" + System.identityHashCode(this) + " [name=" + this.name + ", alias=" + this.alias + ", func="
				+ this.func + "]";
	}

}
