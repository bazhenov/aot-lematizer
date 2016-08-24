package me.bazhenov.aot;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

public class Lemma {

	private final String base;
	private final List<Flexion> flexions;
	private final PartOfSpeech posTag;
	private final Set<String> prefixes;
	private final Set<String> endings;

	public Lemma(String base, List<Flexion> flexions) {
		this.base = base;
		this.flexions = flexions;
		prefixes = endings = emptySet();
		String posCode = flexions.get(0).getAncode().split(" ", 3)[1];
		try {
			posTag = PosTag.fromString(posCode);
		} catch (RuntimeException e) {
			throw new RuntimeException("Invalid POS: " + base + "/" + posCode, e);
		}
	}

	public Lemma(String base, List<Flexion> flexions, String posCode, Set<String> prefixes, Set<String> endings) {
		this.base = base;
		this.flexions = requireNonNull(flexions);
		this.prefixes = requireNonNull(prefixes);
		this.endings = requireNonNull(endings);
		try {
			posTag = PosTag.fromString(posCode);
		} catch (RuntimeException e) {
			throw new RuntimeException("Invalid POS: " + base + "/" + posCode, e);
		}
	}

	public String getWord() {
		return getWord(flexions.get(0));
	}

	public String getWord(Flexion f) {
		return f.getPrefix() + base + f.getEnding();
	}

	public PartOfSpeech getPosTag() {
		return posTag;
	}

	public String getBase() {
		return base;
	}

	public List<Flexion> getFlexions() {
		return flexions;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
			.add("word", base)
			.toString();
	}

	public Set<String> derivate(String... tags) {
		Set<String> result = newHashSet();
		for (Flexion f : flexions)
			if (isAncodeMatchAllTags(f, tags))
				result.add(base + f.getEnding());
		return result;
	}

	private boolean isAncodeMatchAllTags(Flexion f, String[] tags) {
		for (String t : tags)
			if (!f.getAncode().contains(t))
				return false;
		return true;
	}

	public Set<String> getEndings() {
		return endings;
	}

	public Set<String> getPrefixes() {
		return prefixes;
	}

	public boolean hasFlexionBy(String preffix, String suffix) {
		return flexions.stream()
			.anyMatch(f -> (isNullOrEmpty(preffix) || preffix.equals(f.getPrefix()) &&
				suffix.equals(f.getEnding())
			));
	}
}
