package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class CharacterUtils {

	public static Charset dictionaryCharset = Charset.forName("windows-1251");

	public static byte safeCastCharacter(char n) {
		ByteBuffer encode = dictionaryCharset.encode(new String(new char[]{n}));
		if (encode.limit() != 1) {
			throw new IllegalArgumentException("Invalid character: " + n);
		}
		return encode.get(0);
	}
}
