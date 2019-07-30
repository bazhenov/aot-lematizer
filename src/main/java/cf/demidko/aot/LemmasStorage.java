package cf.demidko.aot;

import me.bazhenov.aot.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LemmasStorage {

	private final byte[][] strings = new byte[171365][];

	// это поле нужно методу addBinaryString
	private int currentItem = -1;

	private void addBinaryString(final byte[] str) {
		strings[++currentItem] = str;
	}

	// оптимизация чтобы не выделять каждый раз память под буффер юникоднгых символов
	private final char[] resultBuffer = new char[36];
	private int resIndex;

	public String get(final int requestIndex) {
		resIndex = -1;
		for (final byte b : strings[requestIndex]) {
			resultBuffer[++resIndex] = Utils.byteToChar(b);
		}
		return String.valueOf(resultBuffer, 0, resIndex + 1);

		// здесь старый вариант преобразования сохраняется на всякий случай
		/*final byte[] src = lemmasData[requestIndex];
		final char[] res = new char[src.length];
		for(int i = 0; i < res.length; ++i){
			res[i] = Utils.byteToChar(src[i]);
		}
		return new String(res);*/
	}

	public LemmasStorage() throws IOException {
		try (final InputStream lemmasReader = getClass().getResourceAsStream("/lemmas.bin")) {
			final byte[] buf = new byte[36];
			int bufIndex = -1;
			while (true) {
				final byte currentByte = (byte) lemmasReader.read();
				if (currentByte == 0) {
					break;
				}
				// байт 95 разделяет массивы из байт Lemmas
				if (currentByte == 95) {
					addBinaryString(Arrays.copyOf(buf, bufIndex + 1));
					bufIndex = -1;
					continue;
				}
				buf[++bufIndex] = currentByte;
			}
		}
	}
}
