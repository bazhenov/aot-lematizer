package com.farpost.aot;

import com.farpost.aot.tab.GrammarInfo;
import me.bazhenov.Utils;

import java.util.List;

public final class Compiler {

	public static byte complieChar(final char n) {
		switch (n) {
			case '\n':
				return 0;
			default:
				return Utils.safeCharToByte(n);
		}
	}

	public static byte[] complieGrammarInfoLine(final List<GrammarInfo> line) {
		final var res = new byte[line.size()];
		for(var i = 0; i < res.length; ++i) {
			res[i] = line.get(i).toByte();
		}
		return res;
	}

	public static byte[] complieString(final String s) {
		final var res = new byte[s.length()];
		for(var i = 0; i < res.length; ++i) {
			res[i] = complieChar(s.charAt(i));
		}
		return res;
	}
}
