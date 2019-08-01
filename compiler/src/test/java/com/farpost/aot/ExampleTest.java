package com.farpost.aot;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class ExampleTest {
    @Test
    public void testsIsWorkingCorrectly() {
        assertThat(1, is(1));
        assertThat(2, not(is(1)));
    }
}
