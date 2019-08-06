package readers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MrdReader extends Utf8ResourceReader {

	public MrdReader() throws UnsupportedEncodingException {
		super("/mrd");
	}

	public long readLong() throws IOException {
		return Long.parseLong(readLine());
	}

	public void skipLines(final long count) throws IOException {
		for (long i = 0l; i < count; ++i) {
			readLine();
		}
	}

	public void skipSection() throws IOException {
		skipLines(readLong());
	}
}
