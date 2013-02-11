package me.bazhenov.aot.codec;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PrefixBlockTest {

	@Test
	public void prefixBlockShouldBeSerializable() {
		Collection<Prefix> prefixes = ImmutableList.of(
			new Prefix("арг", 10),
			new Prefix("брак", 20),
			new Prefix("браж", 30),
			new Prefix("груп", 40)
		);

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		PrefixBlock.writeTo(buffer, prefixes);

		buffer.flip();
		ArrayList<Prefix> actual = newArrayList(PrefixBlock.readFrom(buffer));
		assertThat(actual, equalTo(prefixes));
	}
}
