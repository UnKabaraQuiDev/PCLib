package lu.kbra.pclib.db.domain.view;

import lombok.Data;
import lu.kbra.pclib.db.annotations.view.OrderBy;

@Data
public class ViewOrderStructure {

	private final String column;
	private final OrderBy.Type type;

}
