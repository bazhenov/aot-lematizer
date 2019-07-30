package cf.demidko.aot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

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
			System.out.println(Arrays.stream(res).collect(Collectors.toList()));
		}
	}
}
