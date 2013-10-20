package me.bazhenov.aot;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;

import static me.bazhenov.aot.TernaryTreeDict.mergeIntersect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TernaryTreeDictTest {

	@Test
	public void lookup() throws IOException, InterruptedException {
		TernaryTreeDict d = new TernaryTreeDict();
		Set<Lem> lemmas = d.lookupWord("убегаешь");
		System.out.println(Iterables.getFirst(lemmas, null).getPosTag());
	}

	@Test
	public void intersect() {
		IntArrayList a = new IntArrayList(new int[]{1, 2, 4, 6});
		IntArrayList b = new IntArrayList(new int[]{0, 2, 5, 6});

		IntArrayList result = mergeIntersect(a, b);
		assertThat(result.toIntArray(), is(new int[]{2, 6}));
	}
}

