package com.farpost.aot;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BinaryWritingTest {

	@Test
	public void byteWritingWorkingCorrectly() throws IOException {
		final var file = new File("test.bin");
		Files.write(file.toPath(), new byte[]{ 25 });
		assertThat(Files.exists(file.toPath()), is(true));
		assertThat(Files.readAllBytes(Path.of("test.bin")), is(new byte[]{ 25 }));
	}
}
