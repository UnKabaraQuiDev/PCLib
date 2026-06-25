package lu.kbra.pclib.db.impl;

@FunctionalInterface
public interface SQLNamed {

	SQLNamed MOCK = () -> "[NAME]";

	String getName();

}
