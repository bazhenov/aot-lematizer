package com.farpost.aot.data;

import java.util.List;

/**
 * Это - класс данных.
 * Поэтому все его поля должны рассмматриваться не как его поля, а как свойства из C# или Kotlin.
 * Ему не нужны гетеры и сетеры.
 * В данном случае они только усложнят код и замедлят скорость компиляции.
 */
public class Flexion {
	String source;
	List<MorphologyTag> grammatic;
}
