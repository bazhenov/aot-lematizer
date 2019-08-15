package com.farpost.aot.compiler;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Компилятор морфологических сущностей
 *
 * @param <T> тип для компиляции
 */
public abstract class Compiler<T> implements Closeable {

	private final String path;
	private int bytesCounter = 0;
	private int currentLineIndex = -1;

	protected final DataOutputStream writer;

	/**
	 * @param resultPath путь где должен быть расположен скомпилированный бинарный файл
	 */
	public Compiler(String resultPath) throws IOException {
		path = resultPath;
		writer = new DataOutputStream(new FileOutputStream(resultPath));
	}

	/**
	 * Метод компилирует объект и возвращает количество байт
	 *
	 * @param line объект который будет скомпилирован в байтовую строку в файле
	 * @return количество байт занимаемых этой строкой
	 */
	protected abstract int pushBytes(T line) throws IOException;

	/**
	 * Метод компилирует объект и возвращает его индекс в бинарном файле
	 *
	 * @param obj объект
	 * @return индекс строки байт в бинарном файле
	 */
	public int compile(T obj) throws IOException {
		bytesCounter += pushBytes(obj);
		return ++currentLineIndex;
	}

	/**
	 * @return путь к скомпилированному файлу
	 */
	public String getPathToCompiledFile() {
		return path;
	}

	/**
	 * @return количество байт в компилируемом файле на данный момент
	 */
	public int getBytesCounter() {
		return bytesCounter;
	}

	/**
	 * @return количество скомпилированных строк
	 */
	public int getLinesCounter() {
		return currentLineIndex + 1;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
