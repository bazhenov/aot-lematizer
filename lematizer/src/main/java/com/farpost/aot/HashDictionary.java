package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.farpost.aot.Reader.readMorphLine;
import static com.farpost.aot.Reader.readStringLine;

public class HashDictionary {
    private final MorphologyTag[][] morph;
    private final String[] strings;
    private final MiniFlexion[][] lemmas;
    private final Map<Integer, Set<Integer>> hashToIndexOfLemma;


    public HashDictionary() throws IOException {
        try (DataInputStream file = new DataInputStream(getClass().getResourceAsStream("/MRD.BIN"))) {
            morph = readMorph(file);
            strings = readStrings(file);
        }
    }

    private static MorphologyTag[][] readMorph(DataInputStream file) throws IOException {
        MorphologyTag[][] res = new MorphologyTag[file.readInt()][];
        for (int i = 0; i < res.length; ++i) {
            res[i] = readMorphLine(file);
        }
        return res;
    }

    private static String[] readStrings(DataInputStream file) throws IOException {
        String[] res = new String[file.readInt()];
        for (int i = 0; i < res.length; ++i) {
            res[i] = readStringLine(file);
        }
        return res;
    }

    private static
}
