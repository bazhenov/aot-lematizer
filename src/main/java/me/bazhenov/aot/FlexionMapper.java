package me.bazhenov.aot;

import java.util.ArrayList;
import java.util.List;

public class FlexionMapper implements Mapper<String, List<Flexion>> {

	public List<Flexion> map(String input) {
		List<Flexion> flexions = new ArrayList<Flexion>();
		for (String flexion : input.split("%")) {
			if (flexion.isEmpty()) {
				continue;
			}
			String[] parts = flexion.split("\\*");
			flexions.add(new Flexion(parts[0].toLowerCase().replace("ё", "е"), parts[1].substring(0, 2)));
		}
		return flexions;
	}
}
