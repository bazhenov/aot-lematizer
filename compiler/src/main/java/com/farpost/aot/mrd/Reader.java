package com.farpost.aot.mrd;

import com.farpost.aot.Utf8ResourceReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Reader extends Utf8ResourceReader {

	public Reader() throws UnsupportedEncodingException {
		super("/mrd");
	}

	public long readLong() throws IOException {
		return Long.parseLong(bufReader.readLine());
	}

	public String readLine() throws IOException {
		return bufReader.readLine();
	}


	public void skipLines(final long count) throws IOException {
		for (long i = 0l; i < count; ++i) {
			bufReader.readLine();
		}
	}

	public void skipSection() throws IOException {
		skipLines(readLong());
	}
}
