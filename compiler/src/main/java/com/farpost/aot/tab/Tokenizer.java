package com.farpost.aot.tab;

public final class Tokenizer {

    public static String getAncode(final String line) {
        return line.substring(0, 2);
    }

    public static String[] getInfo(final String line) {
        return line.substring(5).split(" |,");
    }
}
