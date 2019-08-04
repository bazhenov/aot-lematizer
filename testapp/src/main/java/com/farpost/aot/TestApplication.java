package com.farpost.aot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestApplication {

    public static void main(String[] args) throws IOException {
        final FlexionStorage flexionStorage = new FlexionStorage();
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in))
        ) {
            while (true) {
                System.out.println(Arrays.stream(
                        flexionStorage.get(reader.readLine())
                        ).collect(Collectors.toList())
                );
            }
        }
    }
}
