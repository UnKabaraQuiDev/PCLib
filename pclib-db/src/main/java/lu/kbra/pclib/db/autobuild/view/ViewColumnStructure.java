package lu.kbra.pclib.db.autobuild.view;

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

	public ViewColumnStructure(final String name, final String alias, final String func) {
		this.name = name;
		this.alias = alias;
		this.func = func;
	}

	public ViewColumnStructure() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public String getFunc() {
		return this.func;
	}

	public void setFunc(final String func) {
		this.func = func;
	}

	@Override
	public String toString() {
		return "ViewColumnStructure@" + System.identityHashCode(this) + " [name=" + this.name + ", alias=" + this.alias + ", func="
				+ this.func + "]";
	}

}
