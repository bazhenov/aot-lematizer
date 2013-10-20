package me.bazhenov.aot;

import java.util.Set;

public interface Dictionary {

	Set<Lem> lookupWord(String word);
}
