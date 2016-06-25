package me.bazhenov.aot;

import com.google.common.base.Function;
import com.google.common.base.Objects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Lemma {

	public static Function<Lemma, String> retireveWord = new Function<Lemma, String>() {
		@Override
		public String apply(Lemma lemma) {
			return lemma.getWord();
		}
	};

	private final String base;
	private final List<Flexion> flexions;
	private final PartOfSpeech posTag;
	private Set<String> prefixes = new HashSet<>();
	private Set<String> endings = new HashSet<>();

	public Lemma(String base, List<Flexion> flexions) {
		this.base = base;
		this.flexions = flexions;
		String posCode = flexions.get(0).getAncode().split(" ", 3)[1];
		try {
			posTag = PosTag.fromString(posCode);
		} catch (Exception e) {
			throw new RuntimeException("Invalid POS: " + base + "/" + posCode, e);
		}
	}

	public Lemma(String base, List<Flexion> flexions, String posCode) {
		this.base = base;
		this.flexions = flexions;
		try {
			posTag = PosTag.fromString(posCode);
		} catch (Exception e) {
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
		return Objects.toStringHelper(this)
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

	public Lemma setEndings(Set<String> endings) {
		this.endings = endings;
		return this;
	}

	public Set<String> getPrefixes() {
		return prefixes;
	}

	public Lemma setPrefixes(Set<String> prefixes) {
		this.prefixes = prefixes;
		return this;
	}
}
