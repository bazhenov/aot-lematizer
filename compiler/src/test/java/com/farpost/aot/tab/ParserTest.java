package com.farpost.aot.tab;

import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParserTest {

    @Test
    public void ancodeParsingTest() {
        assertThat(Parser.parseAncode("Ют A С мр,ед,тв,разг"), is(equalTo("Ют")));
        assertThat(Parser.parseAncode("Эл a Г дст,прш,мн,арх"), is(equalTo("Эл")));
        assertThat(Parser.parseAncode("Эп a Г дст,буд,2л,мн,арх"), is(equalTo("Эп")));
        assertThat(Parser.parseAncode("кн a ДЕЕПРИЧАСТИЕ дст,нст"), is(equalTo("кн")));
        assertThat(Parser.parseAncode("Эу a ДЕЕПРИЧАСТИЕ дст,прш,арх"), is(equalTo("Эу")));
        assertThat(Parser.parseAncode("къ a Г дст,пвл,1л,ед"), is(equalTo("къ")));
    }

    @Test
    public void infoParsingTest() {
        assertThat(Parser.parseInfo("Ют A С мр,ед,тв,разг"), containsInAnyOrder(
                GrammarInfo.fromString("тв"),
                GrammarInfo.fromString("С"),
                GrammarInfo.fromString("ед"),
                GrammarInfo.fromString("мр"),
                GrammarInfo.fromString("разг")
        ));
        assertThat(Parser.parseInfo("Эл a Г дст,прш,мн,арх"), containsInAnyOrder(
                GrammarInfo.fromString("Г"),
                GrammarInfo.fromString("дст"),
                GrammarInfo.fromString("прш"),
                GrammarInfo.fromString("мн"),
                GrammarInfo.fromString("арх")
        ));
        assertThat(Parser.parseInfo("Эп a Г дст,буд,2л,мн,арх"), containsInAnyOrder(
                GrammarInfo.fromString("буд"),
                GrammarInfo.fromString("Г"),
                GrammarInfo.fromString("дст"),
                GrammarInfo.fromString("2л"),
                GrammarInfo.fromString("мн"),
                GrammarInfo.fromString("арх")
        ));

        assertThat(Parser.parseInfo("кн a ДЕЕПРИЧАСТИЕ дст,нст"), containsInAnyOrder(
                GrammarInfo.fromString("ДЕЕПРИЧАСТИЕ"),
                GrammarInfo.fromString("дст"),
                GrammarInfo.fromString("нст")
        ));
        assertThat(Parser.parseInfo("Эу a ДЕЕПРИЧАСТИЕ дст,прш,арх"), containsInAnyOrder(
                GrammarInfo.fromString("ДЕЕПРИЧАСТИЕ"),
                GrammarInfo.fromString("дст"),
                GrammarInfo.fromString("прш"),
                GrammarInfo.fromString("арх")
        ));
        assertThat(Parser.parseInfo("къ a Г дст,пвл,1л,ед"), containsInAnyOrder(
                GrammarInfo.fromString("1л"),
                GrammarInfo.fromString("Г"),
                GrammarInfo.fromString("дст"),
                GrammarInfo.fromString("пвл"),
                GrammarInfo.fromString("ед")
        ));
    }
}
