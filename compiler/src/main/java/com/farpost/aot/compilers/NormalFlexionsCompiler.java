package com.farpost.aot.compilers;

import com.farpost.aot.Flexion;

import java.io.IOException;
import java.util.Collection;

public class NormalFlexionsCompiler extends Compiler<Collection<Flexion>> {

	/**
	 * @param resultPath путь где должен быть расположен скомпилированный бинарный файл
	 */
	public NormalFlexionsCompiler() throws IOException {
		super("target/COLLISIONS.BIN");
	}

	@Override
	protected int pushBytes(Collection<Flexion> line) throws IOException {
		return 0;
	}
}
