package com.farpost.aot;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.farpost.aot.Bytecode.isContent;

public class Reader {
    /**
     * оптимизация чтобы не выделять каждый раз память под буфер юникодных символов
     * 36 - длина наибольшей флексии, поэтому больше нам и не потребуется
     */
    private static final char[] strBuf = new char[36];
    /**
     * Аналогичная оптимизация для грамматики
     */
    private static final MorphologyTag[] grmBuf = new MorphologyTag[12];

    /**
     * Читает из потока байт юникодную строку
     *
     * @param reader поток байт
     * @return строка
     */
    public static synchronized String readStringLine(DataInputStream reader) throws IOException {
        int index = -1;
        for (byte j = reader.readByte(); isContent(j); j = reader.readByte()) {
            strBuf[++index] = Utils.byteToChar(j);
        }
        return String.valueOf(strBuf, 0, index + 1);
    }

    /**
     * Читает из потока байт набор грамматики
     *
     * @param reader поток байт
     * @return набор грамматики
     * @throws IOException
     */
    public static synchronized MorphologyTag[] readMorphLine(DataInputStream reader) throws IOException {
        int bufIndex = -1;
        // считываем строку
        for (byte currentByte = reader.readByte(); isContent(currentByte); currentByte = reader.readByte()) {
            grmBuf[++bufIndex] = MorphologyTag.values()[currentByte];
        }
        return Arrays.copyOf(grmBuf, bufIndex + 1);
    }

}
