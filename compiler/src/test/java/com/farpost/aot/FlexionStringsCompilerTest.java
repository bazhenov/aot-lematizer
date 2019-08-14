package com.farpost.aot;

import com.farpost.aot.compilers.Compiler;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FlexionStringsCompilerTest {

	@Test
	public void compilationIsCorrect() throws IOException {

		final int normalSize = ("яблоко" + "Варенье" + "мёд").length() + 3; // +3 символа \n

		try (var c = CompilerFabric.createCompilerFor(String.class)) {
			c.compile("яблоко");
			c.compile("Варенье");
			c.compile("мёд");
			assertThat(c.getBytesCounter(), equalTo(normalSize));
			try (var reader = new FileInputStream(c.getPathToCompiledFile())) {
				assertThat(reader.readAllBytes().length, equalTo(normalSize));
			}
		}
	}
}
