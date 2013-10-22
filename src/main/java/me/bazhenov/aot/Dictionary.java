package me.bazhenov.aot;

import java.util.Set;

public interface Dictionary {

	Set<Lemma> lookupWord(String word);
}
