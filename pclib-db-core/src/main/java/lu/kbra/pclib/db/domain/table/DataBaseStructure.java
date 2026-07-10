package lu.kbra.pclib.db.domain.table;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.HintsOwner;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseStructure implements HintsOwner {

	private String name;
	private Map<String, Object> hints;
	private final Set<TableStructure> tableStructures = new HashSet<>();
	private final Set<ViewStructure> viewStructures = new HashSet<>();

}
