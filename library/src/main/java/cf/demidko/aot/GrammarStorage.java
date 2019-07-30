package cf.demidko.aot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class GrammarStorage {

	private final GrammarInfo[][] infoLines = new GrammarInfo[722][];

	// это поле нужно методу addBinaryEnums;
	private int dataIndex = -1;

	private void addBinaryEnums(final byte[] enums) {
		infoLines[++dataIndex] = new GrammarInfo[enums.length];
		for (int i = 0; i < enums.length; ++i) {
			infoLines[dataIndex][i] = GrammarInfo.fromByte(enums[i]);
		}
	}

	// Принимает индекс строки с информацией
	public GrammarInfo[] get(final int requestIndex) {
		return infoLines[requestIndex];
	}

	public GrammarStorage() throws IOException {
		try (final InputStream grammarReader = getClass().getResourceAsStream("/grammar.bin")) {
			final byte[] buf = new byte[12];
			final byte endl = 95;
			int bufIndex = -1;
			while (true) {
				final byte currentByte = (byte) grammarReader.read();
				if (currentByte == 0) {
					break;
				}
				if (currentByte == endl) {
					addBinaryEnums(Arrays.copyOf(buf, bufIndex + 1));
					bufIndex = -1;
					continue;
				}
				buf[++bufIndex] = currentByte;
			}
		}
	}
}
