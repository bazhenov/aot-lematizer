package me.bazhenov.aot;

import me.bazhenov.aot.lucene.Morph;

import java.util.Collection;
import java.util.Set;

public interface Dictionary {

	Set<Morph> lookupLemmas(String word);

	Collection<Morph> lookup(String word);
}
