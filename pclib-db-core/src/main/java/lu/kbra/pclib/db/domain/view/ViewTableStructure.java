package lu.kbra.pclib.db.domain.view;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.db.impl.SQLQueryable;

public class ViewTableStructure {

	private String name;
	private String alias;
	private String on;
	private String resolvedTypeName;
	private ViewJoinType joinType = ViewJoinType.MAIN;
	private boolean distinct;
	private Class<? extends SQLQueryable<?>> typeClass;

	private final List<ViewColumnStructure> columns = new ArrayList<>();

	public ViewTableStructure() {
	}

	public ViewTableStructure(
			final String name,
			final String alias,
			final String on,
			final String resolvedTypeName,
			final ViewJoinType joinType,
			final boolean distinct) {
		this.name = name;
		this.alias = alias;
		this.on = on;
		this.resolvedTypeName = resolvedTypeName;
		this.joinType = joinType;
		this.distinct = distinct;
	}

	public String getAlias() {
		return this.alias;
	}

	public List<ViewColumnStructure> getColumns() {
		return this.columns;
	}

	public String getEffectiveName() {
		return this.name != null && !this.name.trim().isEmpty() ? this.name : this.resolvedTypeName;
	}

	public ViewJoinType getJoinType() {
		return this.joinType;
	}

	public String getName() {
		return this.name;
	}

	public String getOn() {
		return this.on;
	}

	public String getResolvedTypeName() {
		return this.resolvedTypeName;
	}

	public Class<? extends SQLQueryable<?>> getTypeClass() {
		return this.typeClass;
	}

	public boolean isDistinct() {
		return this.distinct;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	public void setJoinType(final ViewJoinType joinType) {
		this.joinType = joinType;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOn(final String on) {
		this.on = on;
	}

	public void setResolvedTypeName(final String resolvedTypeName) {
		this.resolvedTypeName = resolvedTypeName;
	}

	public void setTypeClass(final Class<? extends SQLQueryable<?>> typeClass) {
		this.typeClass = typeClass;
	}

	@Override
	public String toString() {
		return "ViewTableStructure@" + System.identityHashCode(this) + " [name=" + this.name + ", alias=" + this.alias + ", on=" + this.on
				+ ", resolvedTypeName=" + this.resolvedTypeName + ", joinType=" + this.joinType + ", distinct=" + this.distinct
				+ ", typeClass=" + this.typeClass + ", columns=" + this.columns + "]";
	}

}
