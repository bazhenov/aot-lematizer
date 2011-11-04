package me.bazhenov.aot;

import com.google.common.base.Function;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BlockTest {

	@Test
	public void testBlock() {
		Block b = createBlock("красного", "красному", "красный", "красный", "красных");
		assertThat(b.getCommonPrefix(), equalTo("красн"));

		assertThat(b.getVariationAtOffset(0), equalTo(new Variation("красного", "ae", 1)));
		//assertThat(b.getVariationAtOffset(1), equalTo("красному"));
		//assertThat(b.getVariationAtOffset(2), equalTo("красный"));
	}

	private Block createBlock(String... words) {
		List<Variation> variations = transform(asList(words), new Function<String, Variation>() {
			public Variation apply(String input) {
				return new Variation(input, "ae", 1);
			}
		});
		return new Block(variations);
	}
}
