package cf.demidko.bflexion;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;

public class FlexionStorage {

	private static int intFromBytes(byte[] b) {
		return b[3] & 0xFF |
			(b[2] & 0xFF) << 8 |
			(b[1] & 0xFF) << 16 |
			(b[0] & 0xFF) << 24;
	}

	private final GrammarStorage grammarStorage = new GrammarStorage();
	private final LemmasStorage lemmasStorage = new LemmasStorage();

	private final Map<Integer, int[]> flexionsData = new HashMap<>();

	public Flexion[] get(final String str) {
		final int[] pointers = flexionsData.get(str.toLowerCase().replace('ё', 'е').hashCode());
		if (pointers == null) {
			return new Flexion[0];
		}
		final Flexion[] results = new Flexion[pointers.length / 2];
		for (int i = 0, j = 0; i < pointers.length; i += 2, ++j) {
			results[j] = new Flexion(
				lemmasStorage.get(pointers[i]),
				grammarStorage.get(pointers[i + 1])
			);
		}
		return results;
	}

	public FlexionStorage() throws IOException {
		try (final InputStream flexionsReader = getClass().getResourceAsStream("/flexions.bin")) {
			final byte[] keybuf = new byte[4], lemmapointer = new byte[4], infopointer = new byte[4];
			for (int i = 0; i < 5017012; ++i) {

				if (flexionsReader.read(keybuf) != 4) {
					throw new UncheckedIOException(new IOException());
				}
				if (flexionsReader.read(lemmapointer) != 4) {
					throw new UncheckedIOException(new IOException());
				}
				if (flexionsReader.read(infopointer) != 4) {
					throw new UncheckedIOException(new IOException());
				}

				final int key = intFromBytes(keybuf);

				final int lemmaValue = intFromBytes(lemmapointer);
				final int infoValue = intFromBytes(infopointer);

				final int[] oldValue = flexionsData.get(key);

				if (oldValue == null) {
					flexionsData.put(key, new int[]{lemmaValue, infoValue});
				} else {
					final int[] joinedValue = new int[oldValue.length + 2];
					System.arraycopy(oldValue, 0, joinedValue, 0, oldValue.length);
					joinedValue[joinedValue.length - 2] = lemmaValue;
					joinedValue[joinedValue.length - 1] = infoValue;
					flexionsData.put(key, joinedValue);
				}
			}
			System.gc();
		}
	}
}
