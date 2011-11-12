package me.bazhenov.aot;

import com.google.common.base.Function;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class BlockTest {

	@Test
	public void testBlock() {
		Block b = createBlock("красного", "красному", "красный", "красный", "красных");
		assertThat(b.getCommonPrefix(), equalTo("красн"));

		assertThat(b.getFirstWord(), equalTo("красного"));
		assertThat(b.getAllVariations(), hasItems(variation("красного", 1), variation("красному", 2),
			variation("красный", 3), variation("красный", 4), variation("красных", 5)));

		assertThat(b.getVariations("красный"), hasItems(variation("красный", 3), variation("красный", 4)));
		assertThat(b.getVariation(5), equalTo(variation("красных", 5)));

		assertThat(b.size(), equalTo(5));
	}

	private Block createBlock(String... words) {
		final AtomicInteger id = new AtomicInteger(1);
		List<Variation> variations = newArrayList(transform(asList(words), new Function<String, Variation>() {
			public Variation apply(String input) {
				return variation(input, id.getAndIncrement());
			}
		}));
		return Block.fromWordList(variations);
	}

	private static Variation variation(String word, int id) {
		Variation variation = new Variation(word, "ае", id);
		variation.setLemmaIndex(1);
		return variation;
	}
}
