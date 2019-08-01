package com.farpost.aot.tab;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TokenizerTest {

    @Test
    public void ancodeIsCorrect() {
        assertThat(Tokenizer.getAncode("Эф A С мр,ед,рд,2"), is(equalTo("Эф")));
        assertThat(Tokenizer.getAncode("гч A С мр,мн,вн,арх"), is(equalTo("гч")));
    }

    @Test
    public void grammarInfoIsCorrect() {
        assertThat(
                Arrays.stream(Tokenizer.getInfo("Эф A С мр,ед,рд,2")).collect(Collectors.toList()),
                hasItems("С", "мр", "ед", "рд", "2"));
        assertThat(Arrays.stream(Tokenizer.getInfo("Эф A С мр,ед,рд,2")).collect(Collectors.toList()),
                hasSize(5));

        assertThat(Tokenizer.getInfo("гч A С мр,мн,вн,арх"), hasItemInArray("вн"));
        assertThat(
                Arrays.stream(Tokenizer.getInfo("гч A С мр,мн,вн,арх")).collect(Collectors.toList()),
                hasSize(5));
    }

}
