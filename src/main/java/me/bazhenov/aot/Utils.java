package me.bazhenov.aot;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class Utils {

	public static Charset dictionaryCharset = Charset.forName("windows-1251");

	public static int writeAndGetBeforePosition(ByteBuffer buffer, Consumer<ByteBuffer> writer) {
		int position = buffer.position();
		writer.accept(buffer);
		return position;
	}

	public static ByteBuffer checkBufferIsReset(ByteBuffer buffer) {
		if (buffer.position() != 0)
			throw new IllegalArgumentException("Buffer should have zero position");
		return buffer;
	}

	static void checkPositive(int l) {
		if (l <= 0) {
			throw new IllegalStateException("Should be positive number: " + l);
		}
	}

	static void checkNonNegative(int l) {
		if (l <= 0) {
			throw new IllegalStateException("Should be positive number: " + l);
		}
	}

	public static byte safeCastCharacter(char n) {
		switch (n) {
			case '-':
				return (byte) 0x2d;
			case '0':
				return (byte) 0x30;
			case '1':
				return (byte) 0x31;
			case '2':
				return (byte) 0x32;
			case '3':
				return (byte) 0x33;
			case '4':
				return (byte) 0x34;
			case '5':
				return (byte) 0x35;
			case '6':
				return (byte) 0x36;
			case '7':
				return (byte) 0x37;
			case '8':
				return (byte) 0x38;
			case '9':
				return (byte) 0x39;
			case '–':
				return (byte) 0x96;
			case '—':
				return (byte) 0x97;
			case 'Ё':
				return (byte) 0xa8;
			case 'ё':
				return (byte) 0xb8;
			case 'А':
				return (byte) 0xc0;
			case 'Б':
				return (byte) 0xc1;
			case 'В':
				return (byte) 0xc2;
			case 'Г':
				return (byte) 0xc3;
			case 'Д':
				return (byte) 0xc4;
			case 'Е':
				return (byte) 0xc5;
			case 'Ж':
				return (byte) 0xc6;
			case 'З':
				return (byte) 0xc7;
			case 'И':
				return (byte) 0xc8;
			case 'Й':
				return (byte) 0xc9;
			case 'К':
				return (byte) 0xca;
			case 'Л':
				return (byte) 0xcb;
			case 'М':
				return (byte) 0xcc;
			case 'Н':
				return (byte) 0xcd;
			case 'О':
				return (byte) 0xce;
			case 'П':
				return (byte) 0xcf;
			case 'Р':
				return (byte) 0xd0;
			case 'С':
				return (byte) 0xd1;
			case 'Т':
				return (byte) 0xd2;
			case 'У':
				return (byte) 0xd3;
			case 'Ф':
				return (byte) 0xd4;
			case 'Х':
				return (byte) 0xd5;
			case 'Ц':
				return (byte) 0xd6;
			case 'Ч':
				return (byte) 0xd7;
			case 'Ш':
				return (byte) 0xd8;
			case 'Щ':
				return (byte) 0xd9;
			case 'Ъ':
				return (byte) 0xda;
			case 'Ы':
				return (byte) 0xdb;
			case 'Ь':
				return (byte) 0xdc;
			case 'Э':
				return (byte) 0xdd;
			case 'Ю':
				return (byte) 0xde;
			case 'Я':
				return (byte) 0xdf;
			case 'а':
				return (byte) 0xe0;
			case 'б':
				return (byte) 0xe1;
			case 'в':
				return (byte) 0xe2;
			case 'г':
				return (byte) 0xe3;
			case 'д':
				return (byte) 0xe4;
			case 'е':
				return (byte) 0xe5;
			case 'ж':
				return (byte) 0xe6;
			case 'з':
				return (byte) 0xe7;
			case 'и':
				return (byte) 0xe8;
			case 'й':
				return (byte) 0xe9;
			case 'к':
				return (byte) 0xea;
			case 'л':
				return (byte) 0xeb;
			case 'м':
				return (byte) 0xec;
			case 'н':
				return (byte) 0xed;
			case 'о':
				return (byte) 0xee;
			case 'п':
				return (byte) 0xef;
			case 'р':
				return (byte) 0xf0;
			case 'с':
				return (byte) 0xf1;
			case 'т':
				return (byte) 0xf2;
			case 'у':
				return (byte) 0xf3;
			case 'ф':
				return (byte) 0xf4;
			case 'х':
				return (byte) 0xf5;
			case 'ц':
				return (byte) 0xf6;
			case 'ч':
				return (byte) 0xf7;
			case 'ш':
				return (byte) 0xf8;
			case 'щ':
				return (byte) 0xf9;
			case 'ъ':
				return (byte) 0xfa;
			case 'ы':
				return (byte) 0xfb;
			case 'ь':
				return (byte) 0xfc;
			case 'э':
				return (byte) 0xfd;
			case 'ю':
				return (byte) 0xfe;
			case 'я':
				return (byte) 0xff;

			default:
				throw new IllegalArgumentException("Illegal character: " + n);
		}
	}
}
