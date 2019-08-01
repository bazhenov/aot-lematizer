package com.farpost.aot.tab;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class ReaderTest extends Reader {

    public ReaderTest() throws UnsupportedEncodingException {
        super();
    }

    @Test
    public void readLineMethodWorksCorrectly() throws IOException {
        assertThat(readLine(), is(equalTo("аа A С мр,ед,им")));
    }
}
