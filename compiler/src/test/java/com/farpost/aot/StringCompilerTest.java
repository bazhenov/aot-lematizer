package com.farpost.aot;

import com.farpost.aot.StringCompiler;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StringCompilerTest {

	@Test
	public void compilationIsCorrect() throws IOException {

		final int normalSize = ("яблоко" + "Варенье" + "мёд").length() + 3;

		try (var c = new StringCompiler()) {
			c.OnString("яблоко");
			c.OnString("Варенье");
			c.OnString("мёд");
			assertThat(c.getSize(), equalTo(normalSize));
		}

		try (var reader = new FileInputStream("STRINGS.BIN")) {
			assertThat(reader.readAllBytes().length, equalTo(normalSize));
		}
	}
}
