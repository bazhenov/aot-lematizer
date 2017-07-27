package me.bazhenov.aot;

import org.testng.annotations.Test;

import static com.google.common.primitives.UnsignedBytes.toInt;
import static me.bazhenov.aot.Utils.safeByteToChar;
import static me.bazhenov.aot.Utils.safeCharToByte;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UtilsTest {

	@Test
	public void characterEncode() {
		assertThat(toInt(safeCharToByte('а')), is(224));
		assertThat(toInt(safeCharToByte('б')), is(225));
		assertThat(toInt(safeCharToByte('-')), is(45));
		assertThat(toInt(safeCharToByte('А')), is(192));
		assertThat(toInt(safeCharToByte('0')), is(48));
	}

	@Test
	public void characterDecode() {
		assertThat(safeByteToChar((byte) 224), is('а'));
		assertThat(safeByteToChar((byte) 225), is('б'));
		assertThat(safeByteToChar((byte) 45), is('-'));
		assertThat(safeByteToChar((byte) 192), is('А'));
		assertThat(safeByteToChar((byte) 48), is('0'));
	}
}