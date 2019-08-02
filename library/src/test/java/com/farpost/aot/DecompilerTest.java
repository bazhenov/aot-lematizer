package com.farpost.aot;

import com.farpost.aot.data.GrammarInfo;
import com.farpost.aot.func.Decompiler;
import me.bazhenov.aot.Utils;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DecompilerTest {

	@Test
	public void stringDecimpilationIsCorrect() {
		char[] str = "тестирование".toCharArray();
		byte[] bytes = new byte[str.length];

		for (int i = 0; i < str.length; ++i) {
			bytes[i] = Utils.charToByte(str[i]);
		}

		assertThat(Decompiler.stringFromBytes(bytes), is(equalTo("тестирование")));
	}

	@Test
	public void stringDecimpilationIsCorrect2() {
		char[] str = "ОченьДлинноеСловоСРазнымиБуквами".toCharArray();
		byte[] bytes = new byte[str.length];

		for (int i = 0; i < str.length; ++i) {
			bytes[i] = Utils.charToByte(str[i]);
		}

		assertThat(Decompiler.stringFromBytes(bytes),
			is(equalTo("ОченьДлинноеСловоСРазнымиБуквами")));
	}

	@Test public void grammarInfoDecompilationIsCorrect() {
		assertThat(Decompiler.infoFromByte((byte) 5), is(equalTo(GrammarInfo.values()[5])));
	}


}
