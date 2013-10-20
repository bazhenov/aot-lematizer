package me.bazhenov.aot;

import java.util.Collection;
import java.util.Set;

public interface Dictionary {

	Set<?> lookupLemmas(String word);

	Collection<?> lookup(String word);
}
