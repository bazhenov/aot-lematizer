package com.farpost.aot;

import java.util.List;
import java.util.Set;

/**
 * Результат сжатия набора лемм
 */
public class ZipResult {
    private final List<Set<MorphologyTag>> morph;
    private final List<String> strings;
    private final List<List<MiniFlexion>> lemmas;

    public ZipResult(List<List<MiniFlexion>> lemmas, List<String> strings, List<Set<MorphologyTag>> morph) {
        this.morph = morph;
        this.strings = strings;
        this.lemmas = lemmas;
    }

    public List<Set<MorphologyTag>> getMorph() {
        return morph;
    }

    public List<String> getStrings() {
        return strings;
    }

    public List<List<MiniFlexion>> getLemmas() {
        return lemmas;
    }
}
