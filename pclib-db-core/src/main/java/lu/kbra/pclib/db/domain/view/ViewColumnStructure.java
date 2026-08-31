package lu.kbra.pclib.db.domain.view;

import lu.kbra.pclib.db.domain.Qualified;

import lombok.Data;

@Data
public class ViewColumnStructure {

	private final String name;
	private final @Qualified String alias;
	private final String func;

}
