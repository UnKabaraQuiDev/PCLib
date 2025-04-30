package lu.pcy113.pclib.db.impl;

import lu.pcy113.pclib.db.annotations.base.DB_Base;
import lu.pcy113.pclib.db.annotations.table.DB_Table;
import lu.pcy113.pclib.db.annotations.view.DB_View;

public interface SQLTypeAnnotated<S> {

	S getTypeAnnotation();

	static String getTypeName(Class<?> type) {
		if(type == null || type == Class.class) {
			return null;
		}
		
		if (type.isAnnotationPresent(DB_Base.class)) {
			return type.getAnnotation(DB_Base.class).name();
		} else if (type.isAnnotationPresent(DB_Table.class)) {
			return type.getAnnotation(DB_Table.class).name();
		} else if (type.isAnnotationPresent(DB_View.class)) {
			return type.getAnnotation(DB_View.class).name();
		}
		return null;
	}

}
