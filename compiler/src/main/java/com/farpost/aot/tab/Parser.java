package com.farpost.aot.tab;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Parser {

    public static String parseAncode(final String line) {
        return Tokenizer.getAncode(line);
    }

    public static List<GrammarInfo> parseInfo(final String line) {
        return Arrays
                .stream(Tokenizer.getInfo(line))
                .map(GrammarInfo::fromString)
                .collect(Collectors.toList());
    }

    public static Map<String, List<GrammarInfo>> parseFromResources() throws IOException {
        final var map = new HashMap<String, List<GrammarInfo>>();
        final var reader = new Reader();
        for (var line = reader.readLine(); line != null; line = reader.readLine()) {
            map.put(parseAncode(line), parseInfo(line));
        }
        reader.close();
        return map;
    }
}
