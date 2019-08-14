package com.farpost.aot;

import com.farpost.aot.compilers.Compiler;
import com.farpost.aot.compilers.LemmaCompiler;
import com.farpost.aot.compilers.MorphologyTagCompiler;
import com.farpost.aot.compilers.FlexionStringsCompiler;

import java.io.IOException;

/**
 * Фабрика компиляторов
 */
public final class CompilerFabric {

	/**
	 * Метод создает компилятор для типа, который нужно скомпилировать
	 *
	 * @param type объект Class для компилируемого типа
	 * @param <T>                 компилируемый тип
	 * @return компилятор для нжуного типа
	 * @throws IOException
	 */
	public static <T> Compiler<T> createCompilerFor(Class<T> type) throws IOException {
		if (type == String.class) {
			return (Compiler<T>) new FlexionStringsCompiler();
		}
		if(type == MorphologyTag[].class) {
			return (Compiler<T>) new MorphologyTagCompiler();
		}
		if(type == Flexion[].class) {
			return (Compiler<T>) new LemmaCompiler();
		}
		throw new IllegalArgumentException();
	}
}
