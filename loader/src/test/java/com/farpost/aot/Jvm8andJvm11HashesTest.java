package com.farpost.aot;

import org.testng.annotations.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Этот тест нужен для проверки эквивалентости метода String.hashCode из JVM 8 (библиотека) и JVM 11 (компилятор)
 */
public class Jvm8andJvm11HashesTest {

	@Test
	public void hashesAreEquivalent() {

		// слева jvm 11
		// справа jvm 8
		assertThat("яблоками".hashCode(),
			equalTo(-1039997097));
		assertThat("тестирование".hashCode(),
			equalTo(721905690));
		assertThat("гиперинфляция".hashCode(),
			equalTo(257392911));
		assertThat("НекоторыеБуквыРусскогоЯзыка".hashCode(),
			equalTo(1260646232));
		assertThat("зелень".hashCode(),
			equalTo(1854048359));

	}
}
