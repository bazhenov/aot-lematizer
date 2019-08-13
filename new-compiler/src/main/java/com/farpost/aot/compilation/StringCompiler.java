package com.farpost.aot.compilation;
import java.io.*;

public class StringCompiler implements Closeable {

	private final DataOutputStream file = new DataOutputStream(new FileOutputStream("STRINGS.BIN"));
	// максимальная длина строки (подсчитано)
	private final byte[] buffer = new byte[37];
	private int bufIndex = -1;
	private int size = 0;

	public StringCompiler() throws FileNotFoundException {

	}

	private void onChar(char ch) {
		buffer[++bufIndex] = me.bazhenov.aot.Utils.charToByte(ch);
	}

	private void onEndl() {
		buffer[++bufIndex] = 100; // байт переноса строки
	}

	public void OnString(String str) throws IOException {
		for(var ch: str.toCharArray()) {
			onChar(ch);
		}
		onEndl();
		file.write(buffer, 0, bufIndex + 1);
		size += (bufIndex + 1);
		bufIndex = -1;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void close() throws IOException {
		file.close();
	}
}
