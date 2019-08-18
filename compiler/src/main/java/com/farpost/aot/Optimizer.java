package com.farpost.aot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Оптимизация
 */
public class Optimizer {

    /**
     * @param zipped результат минификации
     * @return Словарь: хеш -> индексы лемм из списка (коллизии надо проверять)
     */
    public static Map<Integer, Set<Integer>> optimize(ZipResult zipped) {
        var res = new HashMap<Integer, Set<Integer>>();
        for (int i = 0; i < zipped.getLemmas().size(); ++i) {
            var currLemma = zipped.getLemmas().get(i);
            for (var flexion : currLemma) {
                res.computeIfAbsent(
                        // хеш флексии
                        zipped.getStrings().get(flexion.getStringIndex()).hashCode(),
                        // индекс минифицированной леммы
                        k -> new HashSet<>()
                ).add(i);
            }
        }
        return res;
    }
}
