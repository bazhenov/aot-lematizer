package com.farpost.aot;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Флексия - фариант леммы
 */
public class Flexion {

    private final String string;
    private final MorphologyTag[] morphologyTags;

    public Flexion(String string, MorphologyTag[] tags) {
        this.string = string;
        morphologyTags = tags;
    }

    public String getString() {
        return string;
    }

    public Set<MorphologyTag> getTags() {
        return new HashSet<>(asList(morphologyTags));
    }

    @Override
    public String toString() {
        return getString() + getTags();
    }
}
