package me.bazhenov.aot;

import com.google.common.primitives.UnsignedBytes;
import org.testng.annotations.Test;

import static com.google.common.primitives.UnsignedBytes.toInt;
import static me.bazhenov.aot.Utils.safeCastCharacter;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UtilsTest {

	@Test
	public void characterEncode() {
		assertThat(toInt(safeCastCharacter('а')), is(224));
		assertThat(toInt(safeCastCharacter('б')), is(225));
		assertThat(toInt(safeCastCharacter('-')), is(45));
		assertThat(toInt(safeCastCharacter('А')), is(192));
		assertThat(toInt(safeCastCharacter('0')), is(48));
	}
}