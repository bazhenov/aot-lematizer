package com.farpost.aot;

import org.testng.annotations.Test;

import java.io.IOException;

public class FlexionStringsCompilerTest {

	@Test
	public void compilationIsCorrect() throws IOException {

		/*final int normalSize = ("яблоко" + "Варенье" + "мёд").length() + 3; // +3 символа \n

		try (var strCompiler = new FlexionStringsCompiler()) {
			strCompiler.compile("яблоко");
			strCompiler.compile("Варенье");
			strCompiler.compile("мёд");

			assertThat(strCompiler.getBytesCounter(), equalTo(normalSize));

			try (var reader = new FileInputStream(strCompiler.getPathToCompiledFile())) {
				assertThat(reader.readAllBytes().length, equalTo(normalSize));
			}
		}*/
	}
}
