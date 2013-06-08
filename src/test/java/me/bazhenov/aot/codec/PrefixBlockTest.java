package me.bazhenov.aot.codec;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Collection;

import static me.bazhenov.aot.codec.PrefixBlock.createFromSortedList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PrefixBlockTest {

	@Test
	public void prefixBlockShouldBeSerializable() {
		PrefixBlock block = createFromSortedList(ImmutableList.of(
			new Prefix("арг", 10),
			new Prefix("брак", 20),
			new Prefix("браж", 30),
			new Prefix("груп", 40)
		));

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		block.writeTo(buffer);

		buffer.flip();
		PrefixBlock blockCopy = PrefixBlock.readFrom(buffer);
		assertThat(blockCopy, equalTo(block));
	}

	@Test
	public void shouldBeAbleToLookupPrefixesForWord() {
		PrefixBlock block = createFromSortedList(ImmutableList.of(
			new Prefix("арг", 10),
			new Prefix("брак", 20),
			new Prefix("браж", 30),
			new Prefix("браж", 35),
			new Prefix("груп", 40)
		));

		Collection<Prefix> prefixes = block.lookupPrefixes("ааа");
		assertThat(prefixes, hasSize(0));

		prefixes = block.lookupPrefixes("бражка");
		assertThat(prefixes, containsInAnyOrder(new Prefix("браж", 30), new Prefix("браж", 35)));
	}
}
