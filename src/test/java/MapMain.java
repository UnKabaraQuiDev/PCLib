import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;

public class MapMain {

	@Test
	public void castTest() {

		HashMap<Object, Object> map = new HashMap<>();
		map.put(2, "value");
		map.put(4, "value2");
		Object value = map.get(2);
		System.out.println("VALUE: " + value + " type: " + value.getClass());
		System.out.println("MAP: " + map + " type: " + map.getClass());

		Map<Integer, String> newMap = PCUtils.castMap(map, () -> new HashMap<>(), Integer.class, String.class);
		String newValue = newMap.get(2);
		
		System.out.println("NEW VALUE: " + newValue + " type: " + newValue.getClass());
		System.out.println("NEW MAP: v type: " + newMap.getClass());
		
		newMap.entrySet().stream().forEach((e) -> System.out.println(e.getKey() + " " + e.getValue()+" & "+e.getKey().getClass()+" "+e.getValue().getClass()));

	}

}
