package lu.kbra.pclib.db.domain.view;

import lu.kbra.pclib.db.annotations.view.OrderBy;

import lombok.Data;

@Data
public class ViewOrderStructure {

	private final String column;
	private final OrderBy.Type type;

}
