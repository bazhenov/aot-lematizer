package com.farpost.aot;

/**
 * Минифицированная флексия
 */
public class MiniFlexion {
    private final int grammarIndex;
    private final int stringIndex;

    public MiniFlexion(int stringIndex, int grammarIndex) {
        this.grammarIndex = grammarIndex;
        this.stringIndex = stringIndex;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public int getGrammarIndex() {
        return grammarIndex;
    }
}
