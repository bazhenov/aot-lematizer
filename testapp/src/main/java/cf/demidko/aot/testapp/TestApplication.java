package cf.demidko.aot.testapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import cf.demidko.aot.Flexion;
import cf.demidko.aot.FlexionStorage;

public class TestApplication {

	public static void main(String[] args) throws IOException {
		final FlexionStorage flexionStorage = new FlexionStorage();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			final Flexion[] res = flexionStorage.get(reader.readLine());
			if(res == null) {
				System.out.println("[]");
				continue;
			}
			for(final Flexion i: res) {
				System.out.println(i);
			}
		}
	}
}
